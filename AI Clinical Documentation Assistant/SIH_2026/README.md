# Clinical Memory System --- Backend API & Work Division

## 1. Project Overview

**Project:** AI Clinical Documentation Assistant / Clinical Memory
System\
**Backend:** Python + FastAPI + PostgreSQL + SQLAlchemy + Alembic +
Pydantic + JWT/Bearer Authentication.

The system is a human-in-the-loop clinical documentation pipeline:

``` text
Frontend
   ↓
Gateway / Authentication
   ↓
STEP 1 — Input Processing
   ↓ Step1Output
STEP 2 — Clinical NLP
   ↓ ClinicalEvent[]
STEP 3 — Patient Memory Engine
   ↓ RetrievedContext
STEP 4 — Documentation Generation
   ↓ GeneratedDocument
Physician Review
   ↓ memory_write_payload
STEP 3 — Memory Write Gate
```

------------------------------------------------------------------------

# 2. Backend Work Division

## Developer 1 --- Gateway + Authentication + Step 1 + Step 2

Developer 1 owns:

``` text
Gateway
├── FastAPI application
├── Authentication
├── JWT
├── Routing/orchestration
│
├── Step 1 — Input Processing
│   ├── Typed documents
│   ├── Handwritten documents
│   ├── Multilingual input
│   ├── OCR
│   ├── VLM
│   ├── Confidence gating
│   └── Human verification
│
└── Step 2 — Clinical NLP
    ├── Preprocessing
    ├── Abbreviation handling
    ├── Terminology normalization
    ├── NER
    ├── Contextualization
    ├── Event building
    └── Validation
```

**Developer 1 output:** `Step1Output → ClinicalEvent[]`

The `ClinicalEvent[]` schema is the integration boundary with Developer
2.

------------------------------------------------------------------------

## Developer 2 --- Step 3 + Step 4 + Memory Feedback

Developer 2 owns:

``` text
STEP 3 — Patient Memory Engine
├── Memory Write Gate
├── Memory stores
├── Concept threads
├── Trust tiers
├── Conflict detection
├── Retrieval
└── Provenance

STEP 4 — Documentation Generation
├── Context assembly
├── SOAP generation
├── Discharge summary generation
├── Provenance
├── Validation
├── Physician review
├── Regeneration
└── Feedback to memory
```

**Developer 2 flow:**

``` text
ClinicalEvent[]
   ↓
Memory Write Gate
   ↓
Patient Memory
   ↓
RetrievedContext
   ↓
Documentation Generator
   ↓
GeneratedDocument
   ↓
Physician Finalization
   ↓
memory_write_payload
   ↓
Memory Write Gate
```

------------------------------------------------------------------------

# 3. Shared Contract Rules

Both developers use:

``` text
contracts/
├── openapi/
└── schemas/
```

Important shared objects:

``` text
Step1Output
ClinicalEvent
MemoryFact
RetrievedContext
GeneratedDocument
memory_write_payload
```

Do not directly depend on another developer's internal Python classes.
Communicate through the agreed JSON contracts.

------------------------------------------------------------------------

# 4. API Conventions

Local base URL:

``` text
http://127.0.0.1:8000
```

API prefix:

``` text
/api/v1
```

Authentication:

``` http
Authorization: Bearer <access_token>
```

IDs use UUID strings.

Timestamps use ISO-8601.

Confidence values use `0.0` to `1.0`.

------------------------------------------------------------------------

# 5. Gateway / Authentication APIs

**Owner: Developer 1**

## GET `/health`

Checks whether the Gateway is alive.

Response:

``` json
{
  "status": "healthy",
  "service": "gateway"
}
```

------------------------------------------------------------------------

## POST `/api/v1/auth/login`

Authenticates a physician/user.

Request:

``` json
{
  "email": "doctor@example.com",
  "password": "password123"
}
```

Response:

``` json
{
  "access_token": "<JWT_ACCESS_TOKEN>",
  "refresh_token": "<JWT_REFRESH_TOKEN>",
  "token_type": "bearer"
}
```

`401` means credentials are invalid.

------------------------------------------------------------------------

## GET `/api/v1/auth/me`

Returns the currently authenticated user.

Header:

``` http
Authorization: Bearer <access_token>
```

Response:

``` json
{
  "id": "uuid",
  "email": "doctor@example.com",
  "full_name": "Demo Physician",
  "role": "physician",
  "is_active": true
}
```

------------------------------------------------------------------------

## POST `/api/v1/auth/refresh`

Generates a new access token from a valid refresh token.

Request:

``` json
{
  "refresh_token": "<JWT_REFRESH_TOKEN>"
}
```

Response:

``` json
{
  "access_token": "<NEW_ACCESS_TOKEN>",
  "refresh_token": "<JWT_REFRESH_TOKEN>",
  "token_type": "bearer"
}
```

------------------------------------------------------------------------

# 6. STEP 1 --- Input Processing APIs

**Owner: Developer 1**

Purpose: turn medical files/text into normalized, confidence-gated
input.

## POST `/api/v1/step1/documents/typed`

Accepts typed reports.

Content type:

``` text
multipart/form-data
```

Fields:

``` text
patient_id
encounter_id
file
```

Output:

``` text
Step1Output
```

------------------------------------------------------------------------

## POST `/api/v1/step1/documents/handwritten`

Processes handwritten prescriptions/notes.

Fields:

``` text
patient_id
encounter_id
file
```

Output:

``` text
Step1Output
```

Low-confidence/high-risk extraction must be gated for human
verification.

------------------------------------------------------------------------

## POST `/api/v1/step1/documents/multilingual`

Processes multilingual input.

Example:

``` json
{
  "patient_id": "uuid",
  "encounter_id": "uuid",
  "text_input": "Patient has fever and cough for 3 days.",
  "source_language": "en"
}
```

The pipeline preserves source language and translation confidence.

------------------------------------------------------------------------

## GET `/api/v1/step1/documents/{document_id}`

Returns the current Step 1 processing result.

Possible states:

``` text
complete
pending_human_verification
failed
```

------------------------------------------------------------------------

## POST `/api/v1/step1/documents/{document_id}/human-verify`

Allows physician verification/correction.

Request:

``` json
{
  "field_id": "uuid",
  "verified_text": "Corrected clinical value",
  "reviewer_id": "doctor-123",
  "approved": true
}
```

------------------------------------------------------------------------

# 7. STEP 1 → STEP 2: Step1Output

``` json
{
  "document_id": "uuid",
  "patient_id": "uuid",
  "encounter_id": "uuid",
  "input_modality": "typed | handwritten | multilingual",
  "source_language": "en | hi | ta | ...",

  "extracted_fields": [
    {
      "field_id": "uuid",
      "raw_text": "Patient has hypertension",
      "standardized_text": "Patient has hypertension",
      "extraction_confidence": 0.94,
      "is_high_risk_field": false,
      "confidence_tier": "auto_pass",

      "dual_run_result": {
        "triggered": false,
        "second_pass_text": null,
        "agreement": true
      },

      "requires_doctor_review_before_memory_write": false
    }
  ],

  "translation_confidence": 1.0,
  "original_language_text": null,
  "ocr_engine_used": "ocr-engine",
  "vlm_model_used": null,
  "processing_status": "complete",
  "audit_log_id": "uuid",
  "created_at": "iso8601"
}
```

Only appropriate/cleared Step1Output should proceed normally to Step 2.

------------------------------------------------------------------------

# 8. STEP 2 --- Clinical NLP APIs

**Owner: Developer 1**

## POST `/api/v1/step2/process`

Converts Step1Output into structured clinical events.

Request:

``` json
{
  "document_id": "uuid",
  "patient_id": "uuid",
  "encounter_id": "uuid",
  "step1_output": {
    "...": "Step1Output"
  }
}
```

Response:

``` json
{
  "clinical_events": [
    {
      "...": "ClinicalEvent"
    }
  ],
  "patient_id": "uuid",
  "encounter_id": "uuid",
  "source_document_id": "uuid",
  "processed_at": "iso8601"
}
```

------------------------------------------------------------------------

## GET `/api/v1/step2/process/{document_id}`

Returns the previously processed ClinicalEvent objects without
reprocessing.

------------------------------------------------------------------------

# 9. STEP 2 → STEP 3: ClinicalEvent

``` json
{
  "clinical_events": [
    {
      "event_local_id": "uuid",
      "original_text": "Patient has hypertension",
      "processed_text": "Patient has hypertension",
      "normalized_concept": "Hypertension",
      "snomed_ct_id": "38341003",
      "entity_type": "Disease",
      "clinical_domain": "cardiology",
      "relationships": [],
      "assertion": "affirmed",
      "clinical_status": "active",
      "temporal_context": "current",
      "temporal_date": null,
      "bioclinicalbert_confidence": 0.94,
      "gemini_contextualization_confidence": 0.96,
      "source_document_id": "uuid",
      "source_text_span": {
        "start": 0,
        "end": 27
      },
      "input_modality": "typed",
      "source_language": "en",
      "translation_confidence": 1.0,
      "validation_status": "valid"
    }
  ],
  "patient_id": "uuid",
  "encounter_id": "uuid",
  "source_document_id": "uuid",
  "processed_at": "iso8601"
}
```

Critical rule:

``` text
validation_status != valid
        ↓
Do NOT write to normal patient memory.
```

------------------------------------------------------------------------

# 10. STEP 3 --- Patient Memory APIs

**Owner: Developer 2**

## POST `/api/v1/step3/memory/events`

This is the **single Memory Write Gate**.

Request:

``` json
{
  "patient_id": "uuid",
  "encounter_id": "uuid",
  "source": "simulated_abha | patient_upload | physician_approved_consultation",
  "clinical_events": [
    {
      "...": "ClinicalEvent"
    }
  ]
}
```

Response:

``` json
{
  "written_events": [
    {
      "event_id": "uuid",
      "concept_thread_id": "uuid",
      "trust_tier": 1,
      "thread_match_confidence": "high",
      "thread_match_method": "code_system",
      "is_new_thread": false
    }
  ],
  "conflicts_detected": [],
  "rejected_events": []
}
```

**No other module should bypass this endpoint to write clinical
memory.**

------------------------------------------------------------------------

## GET `/api/v1/step3/memory/{patient_id}/events`

Returns the patient's append-only longitudinal event history.

------------------------------------------------------------------------

## GET `/api/v1/step3/memory/{patient_id}/current-state`

Returns the current materialized state of concept threads.

Example:

``` text
Hypertension → active
Metformin → active
Penicillin Allergy → active
```

------------------------------------------------------------------------

# 11. STEP 3 --- Retrieval API

## POST `/api/v1/step3/memory/retrieve`

Retrieves context relevant to the current encounter.

Request:

``` json
{
  "patient_id": "uuid",
  "encounter_id": "uuid",
  "query_concepts": [
    "hypertension",
    "medications",
    "allergies"
  ]
}
```

Response:

``` text
RetrievedContext
```

------------------------------------------------------------------------

# 12. RetrievedContext --- STEP 3 → STEP 4

``` json
{
  "verified_context": {
    "conditions": [],
    "medications": [],
    "allergies": [],
    "procedures": [],
    "lab_trends": [],
    "significant_events": []
  },

  "unverified_information": [],

  "conflicts": []
}
```

Step 4 must preserve the difference between verified and unverified
information.

------------------------------------------------------------------------

# 13. Conflict APIs

**Owner: Developer 2**

## POST `/api/v1/step3/conflicts/{conflict_id}/resolve`

Request:

``` json
{
  "resolution_action": "confirm_event_a | confirm_event_b | keep_unresolved",
  "physician_id": "doctor-123"
}
```

Response:

``` json
{
  "conflict_id": "uuid",
  "status": "resolved",
  "new_event_id": "uuid"
}
```

------------------------------------------------------------------------

## GET `/api/v1/step3/conflicts`

Query parameters:

``` text
patient_id
status
risk_level
```

Example:

``` text
GET /api/v1/step3/conflicts?patient_id=<uuid>&status=unresolved
```

------------------------------------------------------------------------

# 14. Tier 3 Review APIs

**Owner: Developer 2**

## POST `/api/v1/step3/tier3/{event_id}/approve`

Request:

``` json
{
  "physician_id": "doctor-123"
}
```

Response:

``` json
{
  "event_id": "uuid",
  "new_trust_tier": 2,
  "trust_tier_change_event_id": "uuid"
}
```

------------------------------------------------------------------------

## POST `/api/v1/step3/tier3/{event_id}/reject`

Request:

``` json
{
  "physician_id": "doctor-123"
}
```

Response:

``` json
{
  "event_id": "uuid",
  "trust_tier": 3,
  "reviewed_status": "reviewed_rejected"
}
```

------------------------------------------------------------------------

# 15. STEP 4 --- Documentation APIs

**Owner: Developer 2**

## POST `/api/v1/step4/documents/generate`

Combines current consultation events with RetrievedContext to create a
physician-reviewable draft.

Request:

``` json
{
  "patient_id": "uuid",
  "encounter_id": "uuid",
  "document_type": "soap_note | discharge_summary",

  "current_consultation_events": [
    {
      "...": "ClinicalEvent"
    }
  ],

  "retrieved_context": {
    "...": "RetrievedContext"
  },

  "physician_instructions": null
}
```

Response:

``` json
{
  "document_id": "uuid",
  "document_type": "soap_note",
  "status": "draft",

  "sections": {
    "subjective": "string",
    "objective": "string",
    "assessment": "string",
    "plan": "string",
    "patient_identification": null,
    "reason_for_encounter": null,
    "medications": "string",
    "allergies": "string",
    "procedures": null,
    "relevant_history": "string",
    "follow_up": "string"
  },

  "flags_for_physician_review": [],
  "provenance_map": [],

  "validation_result": {
    "passed": true,
    "failures": [],
    "auto_regeneration_attempts": 0
  },

  "generated_at": "iso8601"
}
```

------------------------------------------------------------------------

# 16. Finalize / Review Document

## POST `/api/v1/step4/documents/{document_id}/finalize`

Possible actions:

``` text
accept
edit
reject_regenerate
```

Request:

``` json
{
  "action": "accept",
  "physician_id": "doctor-123",
  "edited_sections": null,
  "regenerate_notes": null
}
```

On accept/edit, return:

``` json
{
  "document_id": "uuid",
  "status": "finalized",
  "finalized_at": "iso8601",

  "memory_write_payload": {
    "patient_id": "uuid",
    "encounter_id": "uuid",
    "source": "physician_approved_consultation",
    "clinical_events": [
      {
        "...": "ClinicalEvent"
      }
    ]
  }
}
```

The caller then sends the payload to:

``` text
POST /api/v1/step3/memory/events
```

Step 4 must not silently write directly to the database.

------------------------------------------------------------------------

# 17. Complete Endpoint Ownership

  -------------------------------------------------------------------------------------------------------------------------
  Owner          Step           Method         Endpoint                                               Purpose
  -------------- -------------- -------------- ------------------------------------------------------ ---------------------
  Dev 1          Gateway        GET            `/health`                                              Health check

  Dev 1          Auth           POST           `/api/v1/auth/login`                                   Login

  Dev 1          Auth           GET            `/api/v1/auth/me`                                      Current user

  Dev 1          Auth           POST           `/api/v1/auth/refresh`                                 Refresh JWT

  Dev 1          Step 1         POST           `/api/v1/step1/documents/typed`                        Typed document

  Dev 1          Step 1         POST           `/api/v1/step1/documents/handwritten`                  Handwritten document

  Dev 1          Step 1         POST           `/api/v1/step1/documents/multilingual`                 Multilingual input

  Dev 1          Step 1         GET            `/api/v1/step1/documents/{document_id}`                Processing result

  Dev 1          Step 1         POST           `/api/v1/step1/documents/{document_id}/human-verify`   Human verification

  Dev 1          Step 2         POST           `/api/v1/step2/process`                                NLP processing

  Dev 1          Step 2         GET            `/api/v1/step2/process/{document_id}`                  Get NLP result

  Dev 2          Step 3         POST           `/api/v1/step3/memory/events`                          Memory Write Gate

  Dev 2          Step 3         GET            `/api/v1/step3/memory/{patient_id}/events`             Patient history

  Dev 2          Step 3         GET            `/api/v1/step3/memory/{patient_id}/current-state`      Current state

  Dev 2          Step 3         POST           `/api/v1/step3/memory/retrieve`                        Context retrieval

  Dev 2          Step 3         POST           `/api/v1/step3/conflicts/{conflict_id}/resolve`        Resolve conflict

  Dev 2          Step 3         GET            `/api/v1/step3/conflicts`                              List conflicts

  Dev 2          Step 3         POST           `/api/v1/step3/tier3/{event_id}/approve`               Approve Tier 3

  Dev 2          Step 3         POST           `/api/v1/step3/tier3/{event_id}/reject`                Reject Tier 3

  Dev 2          Step 4         POST           `/api/v1/step4/documents/generate`                     Generate document

  Dev 2          Step 4         POST           `/api/v1/step4/documents/{document_id}/finalize`       Finalize/regenerate
  -------------------------------------------------------------------------------------------------------------------------

------------------------------------------------------------------------

# 18. Recommended Git Branches

``` text
main
├── backend-dev1
└── backend-dev2
```

Developer 1 works on:

``` text
services/gateway/
services/input-processing/
services/clinical-nlp/
```

Developer 2 works on:

``` text
services/memory-engine/
services/doc-generation/
```

Both coordinate on:

``` text
contracts/
database/
shared/
tests/contract/
```

Suggested commit format:

``` text
feat(auth): implement JWT login
feat(step1): add typed document processing
feat(step2): add clinical event extraction
feat(memory): implement memory write gate
feat(step4): add SOAP generation
fix(auth): correct token validation
test(step3): add conflict tests
docs(api): update endpoint contract
```

------------------------------------------------------------------------

# 19. Recommended Development Order

## Developer 1

``` text
1. Gateway
2. Database connection
3. User model
4. Password hashing
5. JWT
6. Login
7. /auth/me
8. /auth/refresh
9. JWT authentication dependency
10. RBAC
11. Step 1
12. Step1Output validation
13. Step 2
14. ClinicalEvent validation
15. Contract tests
```

## Developer 2

Developer 2 can use mock ClinicalEvent JSON and work independently:

``` text
1. Memory models
2. Memory stores/repositories
3. Memory Write Gate
4. Trust tiers
5. Concept threads
6. Provenance
7. Conflict detection
8. Retrieval
9. RetrievedContext
10. Documentation context assembly
11. SOAP generator
12. Discharge generator
13. Document validator
14. Physician review/finalization
15. memory_write_payload
16. Integration tests
```

------------------------------------------------------------------------

# 20. Integration Milestones

### Milestone 1

``` text
Login
 ↓
JWT
 ↓
Protected endpoint
```

### Milestone 2

``` text
Step 1
 ↓
Step1Output
 ↓
Step 2
 ↓
ClinicalEvent[]
```

### Milestone 3

``` text
ClinicalEvent[]
 ↓
Step 3 Memory Write Gate
 ↓
Memory
 ↓
RetrievedContext
```

### Milestone 4

``` text
RetrievedContext
+
Current ClinicalEvent[]
 ↓
Step 4
 ↓
GeneratedDocument
```

### Milestone 5

``` text
GeneratedDocument
 ↓
Physician Review
 ↓
memory_write_payload
 ↓
Step 3 Memory Write Gate
```

### Final E2E flow

``` text
Login
 ↓
Upload medical document
 ↓
Step 1 extraction
 ↓
Human verification if required
 ↓
Clinical NLP
 ↓
ClinicalEvent[]
 ↓
Memory Write Gate
 ↓
Memory retrieval
 ↓
SOAP/Discharge generation
 ↓
Physician review
 ↓
Finalize
 ↓
Physician-approved facts
 ↓
Memory Write Gate
```

------------------------------------------------------------------------

# 21. Non-Negotiable Architecture Rules

1.  **Human in the loop:** AI output is advisory; physician is final
    authority.
2.  **Single memory write gate:** all clinical memory writes use
    `/step3/memory/events`.
3.  **Append-only history:** never overwrite historical clinical events.
4.  **Provenance:** preserve source document, text span, modality,
    language and confidence.
5.  **Trust separation:** verified and unverified information remain
    separate.
6.  **Conflict visibility:** safety-relevant conflicts cannot be
    silently hidden.
7.  **Documentation is a draft:** generated documents require explicit
    physician action.
8.  **Contracts first:** shared JSON schemas must remain stable while
    developers work in parallel.
9.  **No silent Step 4 database writes:** finalized facts return as
    `memory_write_payload` and go through Step 3.
10. **Test the contracts:** every inter-step JSON handoff needs contract
    tests.

------------------------------------------------------------------------

# 22. Current Progress

The authentication foundation has already been tested successfully:

``` text
[x] PostgreSQL 18.6 installed
[x] clinical_memory database created
[x] users table
[x] Test physician
[x] bcrypt/passlib compatibility fixed
[x] POST /api/v1/auth/login
[x] JWT access token
[x] JWT refresh token
[ ] GET /api/v1/auth/me
[ ] POST /api/v1/auth/refresh
[ ] JWT protection for other APIs
[ ] RBAC
[ ] Step 1
[ ] Step 2
[ ] Step 3
[ ] Step 4
```

The next implementation target for Developer 1 is:

``` text
JWT dependency
    ↓
GET /api/v1/auth/me
    ↓
POST /api/v1/auth/refresh
    ↓
RBAC
```

Then proceed to Step 1 and Step 2.

------------------------------------------------------------------------

# 23. Quick Reference

### Developer 1 owns

``` text
Gateway
Authentication
JWT
RBAC
Step 1
Step 2
```

### Developer 2 owns

``` text
Step 3
Memory
Trust
Conflicts
Retrieval
Step 4
Documentation
Physician review
Memory feedback
```

### Most important handoffs

``` text
Step 1 → Step 2
Step1Output

Step 2 → Step 3
ClinicalEvent[]

Step 3 → Step 4
RetrievedContext

Step 4 → Step 3
memory_write_payload
```

This README is the working backend handoff document. Before changing a
shared endpoint or JSON schema, both developers should agree and update
the contract files first.
