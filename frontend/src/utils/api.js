async function handle401Retry(originalRequestFn, ...args) {
  const response = await originalRequestFn(...args);
  if (response.status === 401) {
    const refreshResponse = await fetch('/auth/auto-login', {
      credentials: 'include',
      method: 'POST',
    });

    if (refreshResponse.ok) {
      return originalRequestFn(...args);
    } else {
      throw new Error('Unauthorized and token refresh failed');
    }
  }

  return response;
}

export async function postRequest(url, data) {
  return handle401Retry(async () => {
    return fetch(url, {
      credentials: 'include',
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
  });
}

export async function getRequest(url, params = {}) {
  return handle401Retry(async () => {
    const queryString = new URLSearchParams(params).toString();
    const fullUrl = queryString ? `${url}?${queryString}` : url;

    return fetch(fullUrl, {
      credentials: 'include',
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
  });
}
