const ip = import.meta.env.VITE_IP_ADDRESS;

async function handleAuthFailureRetry(originalRequestFn, ...args) {
  let response = await originalRequestFn(...args);

  if (response.status === 403 || response.status === 401) {
    const refreshResponse = await fetch(`${ip}/auth/auto-login`, {
      credentials: 'include',
      method: 'POST',
    });

    if (refreshResponse.ok) {
      response = await originalRequestFn(...args);
    } else {
      return refreshResponse;
    }
  }

  return response;
}

function getCsrfTokenFromCookie() {
  const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
  return match ? decodeURIComponent(match[1]) : null;
}

async function ensureCsrfToken() {
  const token = getCsrfTokenFromCookie();
  if (!token) {
    await fetch(`${ip}/csrf`, {
      credentials: 'include',
    });
  }
}

export async function postRequest(url, data) {
  await ensureCsrfToken(); 

  const csrfToken = getCsrfTokenFromCookie();
  return handleAuthFailureRetry(async () => {
    return fetch(url, {
      credentials: 'include',
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken }),
      },
      body: JSON.stringify(data),
    });
  });
}

export async function getRequest(url, params = {}) {
  await ensureCsrfToken(); 

  const csrfToken = getCsrfTokenFromCookie();
  const queryString = new URLSearchParams(params).toString();
  const fullUrl = queryString ? `${url}?${queryString}` : url;

  return handleAuthFailureRetry(async () => {
    return fetch(fullUrl, {
      credentials: 'include',
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...(csrfToken && { 'X-XSRF-TOKEN': csrfToken }),
      },
    });
  });
}
