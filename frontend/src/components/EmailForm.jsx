import React, { useState, useSyncExternalStore, useEffect } from 'react';
import { postRequest, loginRequest } from '../utils/api';
import setThemeColor from '../hooks/useThemeColor';

function EmailForm({ setLoggedIn, setUserInfo }) {

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

    if(!notFound){
      try {
        const response = await loginRequest(ip+'/auth/login', { email, password });

        if (response.ok) {
          console.log(response);
          const json = await response.json();
          if(json.exists === false) {
            setMessage('User not found. Please register.');
            setNotFound(true);
            setExists(false);
          } else {
            setMessage('User logged in.');
            setLoggedIn(true);
            setUserInfo(json);
            setThemeColor('#193cb8');
          }
          return;
        } else if(response.status === 401) {
          setMessage('Incorrect password.');
          return
        } else {
          setMessage('Other issue. Try again later.');
          return
        }
      } catch (err) {
        setMessage('Server error. Try again later.');
      }}
    try {
        if(password !== confirmPassword) {
          setMessage('Passwords do not match.');
          return;
        }
        const response = await postRequest(ip+'/auth/register', { email, password, name });
        if (response.ok) {
          setMessage('User registered, redirecting to login.');
          setTimeout(() => {
            setNotFound(false);
            setPassword('');
            setMessage('');
          }, 2000);
          
        } else if(response.status === 409) {
          setMessage('User already registered, please log in.');
          setTimeout(() => {
            setNotFound(false);
            setExists(true);
            setPassword('');
            setConfirmPassword('');
            setName('');
            setMessage('');
          }, 2000);
        } else {
          setMessage('Other issue. Try again later.');
        }
      } catch (err) {
        setMessage('Server error. Try again later.');
      }
    } 

  return (
      <form  onSubmit={handleSubmit}  className='w-full max-w-125 h-full flex flex-col items-center justify-around font-anton rounded-md text-blue-800 text-4xl'  >
      <h1 className='text-blue-800 font-anton text-5xl text-shadow-lg'>{exists ? 'Login:' : notFound ? 'Register:' : 'Login/Register:'}</h1>
        <input className={'bg-white font-anton rounded-md text-blue-800 w-6/7 pl-3 text-2xl border-blue-800 border-2 ' + (notFound ? 'h-10' : 'h-15')} placeholder='Email' type='email' value={email} onChange={(e) => setEmail(e.target.value)} required />
        <input className={'bg-white font-anton rounded-md text-blue-800 w-6/7 pl-3 text-2xl border-blue-800 border-2 ' + (notFound ? 'h-10' : 'h-15')} placeholder='Password' type='password' value={password} onChange={(e) => setPassword(e.target.value)} required />
        {
          notFound ? <><input className='bg-white font-anton rounded-md text-blue-800 h-10 w-6/7 pl-3 text-2xl border-blue-800 border-2' placeholder='Confirm Password' type='password' value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required />
                        <input className='bg-white font-anton rounded-md text-blue-800 h-10 w-6/7 pl-3 text-2xl border-blue-800 border-2' placeholder='Name' value={name} onChange={(e) => setName(e.target.value)} required />  </>
                     : <></>
        }
        <button type='submit' className='bg-white font-anton rounded-md text-blue-800 hover:bg-gray-200 transition duration-300 h-15 px-10 text-4xl border-blue-800 border-2 flex justify-center items-center'   >Submit</button>
        {message && (<p className='text-blue-800 text-xl mt-2'>{message}</p> )}
      </form>
  );
}

export default EmailForm;
