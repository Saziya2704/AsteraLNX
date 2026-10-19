# UI & Auth UX update

## Auth features (now in UI + API)
| Feature | Behaviour |
|---------|-----------|
| **Remember me** | Checkbox on login. When checked, JWT lifetime = 7 days and token stored in `localStorage`. When unchecked, default short JWT + `sessionStorage` (cleared when browser tab session ends). |
| **Forgot password** | Login page → "Forgot password?" → request OTP by email. |
| **OTP reset** | 6-digit one-time code, 30 minutes, single use. For local/demo the OTP is returned in API `detail` (would be email/SMS only in production). |
| **Reset password** | Same panel: enter OTP + new password (strength rules enforced server-side). |
| **Session handling** | API 401 clears storage and redirects to login. Logout clears token from both storages. |

## Architecture note
- Backend remains **stateless JWT** (no server session cookies).
- "Remember me" changes token TTL + client storage, not HTTP session cookies.
- This matches the original plan (JWT access tokens), not a classic servlet session.

## UI
- Enterprise login split layout, Inter font, animated brand panel, feature cards
- App shell: dark sidebar, sticky topbar, KPI hover animations, toast notifications, refined tables

## Not implemented (out of original / demo scope)
- Real email/SMS gateway for OTP delivery
- Server-side refresh-token rotation
- CSRF cookie session stack (not required for pure Bearer JWT SPA)
