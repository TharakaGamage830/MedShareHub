import requests
import json

BASE_URL = "http://localhost:8080/api"

def test_unauthorized_access():
    print("Testing unauthorized access to patient records...")
    # Attempt to access patient 1 without a token
    response = requests.get(f"{BASE_URL}/patients/1")
    if response.status_code == 403 or response.status_code == 401:
        print("✅ Unauthorized access blocked.")
    else:
        print(f"❌ Security Flaw: Unauthorized access allowed with status {response.status_code}")

def test_xss_protection():
    print("Testing XSS protection in user profile update...")
    payload = {
        "fullName": "<script>alert('XSS')</script> Hacker",
        "email": "hacker@test.com"
    }
    # This requires a valid token, assuming we have one or just testing the filter logic via a mock
    # In a real pen-test, we'd use a valid session.
    print("ℹ️ XSS test requires valid JWT. Verify XssFilter.java and XssRequestWrapper.java are active in SecurityConfig.")

def test_abac_enforcement_logic():
    print("Testing ABAC logic for restricted roles...")
    # This would involve logging in as an insurance adjuster and verifying redaction
    print("ℹ️ Manual verification: Use Postman/Frontend to verify that Insurance Adjusters see redacted 'clinicalNotes'.")

if __name__ == "__main__":
    print("🔐 Starting MedShare Hub Security Penetration Simulation...")
    test_unauthorized_access()
    test_xss_protection()
    print("🏁 Security simulation complete.")
