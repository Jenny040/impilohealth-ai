// Thin client for the ImpiloHealth-AI Spring Boot backend.
// In dev, Vite proxies /api to http://localhost:8080 (see vite.config.ts), so no CORS setup is needed.
const BASE = (import.meta.env.VITE_API_BASE as string | undefined) ?? "";

export type Prediction = {
  available: boolean; method: string; cyclesUsed: number;
  averageCycleLength?: number | null; lastPeriodStart?: string | null; nextPeriodStart?: string | null;
  daysUntilNextPeriod?: number | null; ovulationDate?: string | null;
  fertileWindowStart?: string | null; fertileWindowEnd?: string | null; daysUntilFertileWindow?: number | null;
};
export type AuthResult = { token: string; tokenType: string; expiresInMs: number; fullName: string; email: string };

export const session = {
  get token() { try { return localStorage.getItem("impilo-token"); } catch { return null; } },
  get name() { try { return localStorage.getItem("impilo-name") || ""; } catch { return ""; } },
  get email() { try { return localStorage.getItem("impilo-email") || ""; } catch { return ""; } },
  get firstName() { return (this.name.trim().split(/\s+/)[0]) || ""; },
  save(r: AuthResult) { localStorage.setItem("impilo-token", r.token); localStorage.setItem("impilo-name", r.fullName); localStorage.setItem("impilo-email", r.email); localStorage.setItem("impilo-auth", "true"); },
  clear() { ["impilo-token", "impilo-name", "impilo-email", "impilo-auth"].forEach(k => localStorage.removeItem(k)); },
};

export class ApiError extends Error {
  constructor(message: string, public status: number, public fieldErrors: Record<string, string> = {}) { super(message); }
}

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers: Record<string, string> = { "Content-Type": "application/json", ...(init.headers as Record<string, string> | undefined) };
  if (session.token) headers.Authorization = `Bearer ${session.token}`;
  let res: Response;
  try { res = await fetch(`${BASE}${path}`, { ...init, headers }); }
  catch { throw new ApiError("Cannot reach the server. Is the backend running?", 0); }
  const text = await res.text();
  const body = text ? (() => { try { return JSON.parse(text); } catch { return null; } })() : null;
  if (!res.ok) {
    const fe = (body?.fieldErrors ?? {}) as Record<string, string>;
    const detail = Object.entries(fe).map(([k, v]) => `${k}: ${v}`).join(", ");
    throw new ApiError(detail || body?.message || `Request failed (${res.status})`, res.status, fe);
  }
  return body as T;
}

export const api = {
  register: (p: { email: string; password: string; fullName: string; gender: string }) =>
    request<AuthResult>("/api/auth/register", { method: "POST", body: JSON.stringify(p) }),
  login: (p: { email: string; password: string }) =>
    request<AuthResult>("/api/auth/login", { method: "POST", body: JSON.stringify(p) }),
  prediction: () => request<Prediction>("/api/cycle/prediction"),
};
