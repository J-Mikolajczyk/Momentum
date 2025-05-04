export async function postRequest(url, data) {
    try {
      const response = await fetch(url, {
        credentials: 'include',
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      });
  
      return response;
    } catch (error) {
      console.error('POST request failed:', error);
      throw error;
    }
  }

  export async function getRequest(url, params = {}) {
    try {
      const queryString = new URLSearchParams(params).toString();
      const fullUrl = queryString ? `${url}?${queryString}` : url;
  
      const response = await fetch(fullUrl, {
        credentials: 'include',
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });
  
      return response;
    } catch (error) {
      console.error('GET request failed:', error);
      throw error;
    }
  }
  
  
  