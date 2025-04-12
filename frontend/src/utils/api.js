export async function postRequest(url, data) {
    try {
      const response = await fetch(url, {
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
  