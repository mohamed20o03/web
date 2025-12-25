export function toApiError(status, body) {
  const pickMsg =
    body?.message ||
    body?.error ||
    body?.email || // Backend sometimes returns error string in email field
    body?.raw ||
    "Request failed";

  if (status === 400) return { kind: "validation", message: pickMsg, body };
  if (status === 401) return { kind: "unauthorized", message: pickMsg, body };
  if (status === 403) return { kind: "forbidden", message: pickMsg, body };
  if (status === 404) return { kind: "notFound", message: pickMsg, body };
  if (status === 413) return { kind: "payloadTooLarge", message: pickMsg, body };
  if (status >= 500) return { kind: "server", message: "Server error", body };
  return { kind: "unknown", message: pickMsg, body };
}
