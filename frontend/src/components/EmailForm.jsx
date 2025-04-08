import React, { useState, useSyncExternalStore } from 'react';
import { postRequest } from '../utils/api';

function EmailForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [exists, setExists] = useState(false);
  const [notFound, setNotFound] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if(!exists) {
      try {
        const response = await postRequest('http://localhost:8080/user/exists', { email });

        if (response.ok) {
          setMessage('User exists. Please enter your password.');
          setExists(true);
        } else if(response.status === 404) {
          setMessage('User not found. Please register.');
          setNotFound(true);
        } else {
          setMessage('Other issue. Try again later.');
        }
      } catch (err) {
        setMessage('Server error. Try again later.');
      }
    } else {
      try {
        const response = await postRequest('http://localhost:8080/user/login', { email, password });

        if (response.ok) {
          setMessage('User logged in.');
        } else {
          setMessage('Wrong password.');
        }
      } catch (err) {
        setMessage('Server error. Try again later.');
      }
    } 
  };

  return (
    <div className='h-screen flex flex-col items-center justify-center bg-gradient-to-b from-blue-400 to-blue-900'>
      <form  onSubmit={handleSubmit}  className='h-1/3 w-5/6 max-w-md max-h-md flex flex-col items-center justify-evenly bg-white font-anton rounded-md text-blue-900 text-4xl border-blue-900 border-2'  >
        <h1 className='text-blue-900 font-anton text-4xl'>Login/Register:</h1>
        <input className='bg-white font-anton rounded-md text-blue-900 h-1/4 w-6/7 pl-3 text-2xl border-blue-900 border-2' placeholder='Email' value={email} onChange={(e) => setEmail(e.target.value)} required />
        {
          exists ? <input className='bg-white font-anton rounded-md text-blue-900 h-1/4 w-6/7 pl-3 text-2xl border-blue-900 border-2' placeholder='Password' value={password} onChange={(e) => setPassword(e.target.value)} required /> : <></>
        }
        <button type='submit' className='bg-white font-anton rounded-md text-blue-900 hover:bg-gray-200 transition duration-300 h-1/4 px-10 text-4xl border-blue-900 border-2'   >Submit</button>
        {message && (<p className='text-blue-900 text-xl mt-2'>{message}</p> )}
      </form>
    </div>
  );
}

export default EmailForm;
