# MedShare Hub - Frontend

> Secure, Accessible, and Patient-Centric Healthcare Interface

## Overview

The MedShare Hub frontend is a React-based web application designed for secure healthcare data exchange. It provides dedicated portals for doctors (Dashboard) and patients (Portal), implementing advanced security features like MFA and fine-grained ABAC authorization feedback.

## Key Features

- 🔐 **Multi-Factor Authentication (MFA)** - Integrated verification flow after initial login
- 🏥 **Provider Dashboard** - Patient search, medical record management, and anomaly alerts
- 👤 **Patient Portal** - Self-access to records, consent management, and secure messaging
- 💬 **Secure Message Center** - Internal encrypted messaging between providers and patients
- 🛡️ **ABAC Authorization Feedback** - UI-level enforcement of Attribute-Based Access Control
- ♿ **WCAG 2.1 AA Compliant** - High accessibility standards with full ARIA support
- 📊 **Compliance Reporting** - Automated generation of HIPAA-related access reports

## Technology Stack

- **Framework**: React 18 with Vite
- **Language**: TypeScript
- **Styling**: Material-UI (MUI) v6
- **State Management**: Redux Toolkit
- **API Client**: Axios with interceptors
- **Icons**: MUI Icons

## Project Structure

```text
src/
├── api/             # API client and interceptors
├── components/      # Reusable UI components
│   ├── auth/        # Login and MFA
│   ├── dashboard/   # Provider-specific components
│   ├── portal/      # Patient-specific components
│   ├── messages/    # Messaging system
│   └── audit/       # Security and logging components
├── pages/           # Main page views (Dashboard, PatientPortal)
├── services/        # Business logic and API abstraction
├── store/           # Redux slices and store configuration
└── theme/           # MUI theme definition
```

## Setup & Running

### Prerequisites

- Node.js 18+
- npm or yarn

### Installation

```bash
npm install
```

### Running Locally

```bash
npm run dev
```

### Building for Production

```bash
npm run build
```

## Accessibility Standards

This project follows **WCAG 2.1 AA** guidelines:
- Full keyboard navigation support
- Semantic HTML landmarks (`<main>`, `<nav>`, `<aside>`)
- Detailed ARIA attributes for dynamic content
- Screen reader-friendly tables and forms
- High contrast color palette

## Security Notice

All data displayed in the frontend is subject to strict backend authorization. The UI provides a representation of the **Attribute-Based Access Control (ABAC)** rules, but final enforcement happens at the API level.

---
MedShare Hub © 2026

