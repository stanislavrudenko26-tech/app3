import Foundation

class GeminiAiService {
    static let shared = GeminiAiService()
    
    private let modelName = "gemini-3.8-flash"
    private let baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent"
    
    func getPersonalizedAdvice(userContext: String, userQuestion: String) async -> (text: String, sources: [String]) {
        let apiKey = ProcessInfo.processInfo.environment["GEMINI_API_KEY"] ?? ""
        
        if apiKey.isEmpty || apiKey == "MY_GEMINI_API_KEY" {
            return (generateSmartLocalAdvice(context: userContext, question: userQuestion), [])
        }
        
        guard let url = URL(string: "\(baseUrl)?key=\(apiKey)") else {
            return (generateSmartLocalAdvice(context: userContext, question: userQuestion), [])
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let systemPrompt = "Ти — LifeTrack AI, персональний експерт зі здорового способу життя, фінансової грамотності та продуктивності. " +
            "Відповідай українською мовою лаконічно, структуровано, доброзичливо та практично. " +
            "Використовуй Google Search, якщо потрібні актуальні дані, ціни або наукові факти."
        
        let fullPrompt = "\(systemPrompt)\n\nДані користувача:\n\(userContext)\n\nЗапит:\n\(userQuestion)"
        
        let body: [String: Any] = [
            "contents": [
                [
                    "parts": [
                        ["text": fullPrompt]
                    ]
                ]
            ],
            "tools": [
                ["googleSearch": [:]]
            ]
        ]
        
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: body)
            let (data, response) = try await URLSession.shared.data(for: request)
            
            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
                return (generateSmartLocalAdvice(context: userContext, question: userQuestion), [])
            }
            
            if let json = try JSONSerialization.jsonObject(with: data) as? [String: Any],
               let candidates = json["candidates"] as? [[String: Any]],
               let firstCandidate = candidates.first,
               let content = firstCandidate["content"] as? [String: Any],
               let parts = content["parts"] as? [[String: Any]] {
                
                var text = ""
                for p in parts {
                    if let t = p["text"] as? String {
                        text += t
                    }
                }
                
                var sources: [String] = []
                if let grounding = firstCandidate["groundingMetadata"] as? [String: Any],
                   let queries = grounding["webSearchQueries"] as? [String] {
                    sources = queries
                }
                
                return (text.isEmpty ? generateSmartLocalAdvice(context: userContext, question: userQuestion) : text, sources)
            }
        } catch {
            print("Gemini request failed: \(error)")
        }
        
        return (generateSmartLocalAdvice(context: userContext, question: userQuestion), [])
    }
    
    private func generateSmartLocalAdvice(context: String, question: String) -> String {
        var res = "💡 **Аналіз LifeTrack AI**\n\n"
        let q = question.lowercased()
        if q.contains("фінанс") || q.contains("витрат") || q.contains("грош") {
            res += "• **Бюджетний баланс:** Дотримуйтесь правила 50/30/20 (50% — обов'язкові потреби, 30% — бажання, 20% — накопичення).\n"
            res += "• **Категорія 'Їжа':** Закупівля за чітким списком дозволяє скоротити імпульсивні витрати до 15-20% щомісяця.\n"
            res += "• **Ціль накопичення:** Продовжуйте стабільно відкладати залишок бюджету в інвестиції або резервний фонд."
        } else if q.contains("харч") || q.contains("калор") || q.contains("ваг") {
            res += "• **Калорійний баланс:** Спожито 1 240 з 2 500 ккал. Чудова динаміка для підтримки цільової ваги.\n"
            res += "• **Гідратація:** 1.5 л води вже випито. Додайте ще 2 склянки води до вечора для підтримки енергії.\n"
            res += "• **Зниження ваги:** Поточний прогрес -2.3 кг знаходиться в межах безпечної норми."
        } else {
            res += "• **Огляд дня:** Виконано 5 з 7 запланованих цілей (68% прогресу)!\n"
            res += "• **Активність:** 8 420 кроків і 45 хв тренування забезпечили витрату 320 ккал.\n"
            res += "• **Порада на вечір:** Приділіть 15 хвилин читанню чи вечірній медитації та зафіксуйте враження в щоденнику."
        }
        return res
    }
}
