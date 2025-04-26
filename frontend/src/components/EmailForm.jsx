import React, { useState, useSyncExternalStore, useEffect } from 'react';
import { postRequest } from '../utils/api';
import setThemeColor from '../hooks/useThemeColor'

function EmailForm({ setLoggedIn, setUserInfo }) {

  useEffect(() => {
    setThemeColor('#51a2ff');
  }, []);

  const ip = import.meta.env.VITE_IP_ADDRESS;
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [message, setMessage] = useState('');
  const [exists, setExists] = useState(false);
  const [notFound, setNotFound] = useState(false);
  const [name, setName] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();

    if(exists) {
      try {
        const response = await postRequest(ip+'/user/login', { email, password });

        if (response.ok) {
          setMessage('User logged in.');
          setLoggedIn(true);
          const json = await response.json();
          setUserInfo(json);
        } else {
          setMessage('Wrong password.');
        }
      } catch (err) {
        setMessage('Server error. Try again later.');
      }
    } else if(notFound) {
      if(password != confirmPassword) {
        setMessage('Passwords do not match!');
        return;
      }
      try {
        const response = await postRequest(ip+'/user/register', { email, password, name });
        if (response.ok) {
          setMessage('User registered, redirecting to login.');
          setTimeout(() => {
            setNotFound(false);
            setPassword('');
            setMessage('');
          }, 2000);
          
        } else if(response.status === 409) {
          setMessage('User already registered, please log in.');
        } else {
          setMessage('Other issue. Try again later.');
        }
      } catch (err) {
        setMessage('Server error. Try again later.');
      }
    } else {
      try {
        const response = await postRequest(ip+'/user/exists', { email });

        if (response.ok) {
          setMessage('Please enter your password.');
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
    } 
  };

  return (
    <div className='h-screen flex flex-col items-center justify-center bg-gradient-to-b from-blue-400 to-blue-900'>
      <form  onSubmit={handleSubmit}  className='min-h-1/3 w-5/6 max-w-md max-h-md flex flex-col items-center justify-evenly bg-white font-anton rounded-md text-blue-900 text-4xl border-blue-900 border-2'  >
      <h1 className='text-blue-900 font-anton text-4xl'>{exists ? 'Login:' : notFound ? 'Register:' : 'Login/Register:'}</h1>
        <input className='bg-white font-anton rounded-md text-blue-900 h-1/4 w-6/7 pl-3 text-2xl border-blue-900 border-2' placeholder='Email' type='email' value={email} onChange={(e) => setEmail(e.target.value)} required />
        {
          (exists || notFound) ? <input className='bg-white font-anton rounded-md text-blue-900 h-1/4 w-6/7 pl-3 text-2xl border-blue-900 border-2' placeholder='Password' type='password' value={password} onChange={(e) => setPassword(e.target.value)} required /> : <></>
        }
        {
          notFound ? <><input className='bg-white font-anton rounded-md text-blue-900 h-1/4 w-6/7 pl-3 text-2xl border-blue-900 border-2' placeholder='Confirm Password' type='password' value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required />
                        <input className='bg-white font-anton rounded-md text-blue-900 h-1/4 w-6/7 pl-3 text-2xl border-blue-900 border-2' placeholder='Name' value={name} onChange={(e) => setName(e.target.value)} required />  </>
                     : <></>
        }
        <button type='submit' className='bg-white font-anton rounded-md text-blue-900 hover:bg-gray-200 transition duration-300 h-1/4 px-10 text-4xl border-blue-900 border-2'   >Submit</button>
        {message && (<p className='text-blue-900 text-xl mt-2'>{message}</p> )}
      </form>
    </div>
  );
}

export default EmailForm;
