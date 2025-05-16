

const ip = import.meta.env.VITE_IP_ADDRESS;

async function handleAuthFailureRetry(originalRequestFn, ...args) {
  const response = await originalRequestFn(...args);
  if (response.status === 401) {
    const refreshResponse = await fetch(ip + '/auth/auto-login', {
      credentials: 'include',
      method: 'POST',
    });

    if (refreshResponse.ok) {
      return originalRequestFn(...args);
    } else {
      return refreshResponse;
    }
  }

  return response;
}

export async function postRequest(url, data) {
  return handleAuthFailureRetry(async () => {
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
  return handleAuthFailureRetry(async () => {
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

export async function loginRequest(url, data) {
    return fetch(url, {
      credentials: 'include',
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    });
}

export async function logoutRequest(email) {
  try {
    return fetch(ip+'/auth/logout', {
      credentials: 'include',
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(email),
    });
    } catch (err) {
      console.log(err);
  }
}
