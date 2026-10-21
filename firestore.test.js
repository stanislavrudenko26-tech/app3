const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read user profile or subcollections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("transactions").get());
});

test("Cross-user isolation: Alice cannot read or write Bob's transactions", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("transactions").get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("transactions").doc("t1").set({
    userId: BOB_UID,
    type: "income",
    amount: 100,
    category: "Salary",
    date: "2026-10-21",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

test("Authenticated user: Alice can create and read her own transaction", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("transactions").doc("t1").set({
    userId: ALICE_UID,
    type: "expense",
    amount: 250.5,
    category: "Їжа",
    date: "2026-10-21",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("transactions").get());
});

test("Schema validation: Invalid transaction type fails", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(ALICE_UID).collection("transactions").doc("t2").set({
    userId: ALICE_UID,
    type: "invalid_type",
    amount: 100,
    category: "Їжа",
    date: "2026-10-21",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});
