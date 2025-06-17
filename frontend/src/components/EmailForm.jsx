import React, { useState, useSyncExternalStore, useEffect } from 'react';
import { postRequest, loginRequest } from '../utils/api';
import setThemeColor from '../hooks/useThemeColor';
import PolicyPopup from './popups/PolicyPopup';
import { motion } from 'framer-motion';
import { useUser } from '../contexts/UserContext';

function EmailForm({ }) {

  const { setLoggedIn, setUserInfo } = useUser();

  const ip = import.meta.env.VITE_IP_ADDRESS;
  const [type, setType] = useState('');
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
        } else if(response.status === 401) {
          setMessage('Incorrect password.');
        } else {
          setMessage('Other issue. Try again later.');
        }
      } catch (err) {
        setMessage('Server error. Try again later.');
      }
      return;
    }
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
    <>
      <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} transition={{ duration: 2 }} className='w-full h-5/12 flex items-center justify-center' >
        <div className='w-full max-w-125 pt-10 h-full flex flex-col items-center justify-around font-anton rounded-md text-blue-800 text-4xl' >
          <form  onSubmit={handleSubmit} className='w-6/7 flex flex-col items-center justify-center gap-4'>
            <h1 className='text-blue-800 font-anton text-5xl text-shadow-lg'>{exists ? 'Login:' : notFound ? 'Register:' : 'Login/Register:'}</h1>
              <input className={'bg-white font-anton rounded-md text-blue-800 w-6/7 pl-3 text-2xl border-blue-800 border-2 h-12'} placeholder='Email' type='email' value={email} onChange={(e) => setEmail(e.target.value)} required />
              <input className={'bg-white font-anton rounded-md text-blue-800 w-6/7 pl-3 text-2xl border-blue-800 border-2 h-12'} placeholder='Password' type='password' value={password} onChange={(e) => setPassword(e.target.value)} required />
              {notFound && (<><input className='bg-white font-anton rounded-md text-blue-800 h-12 w-6/7 pl-3 text-2xl border-blue-800 border-2' placeholder='Confirm Password' type='password' value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required />
                              <input className='bg-white font-anton rounded-md text-blue-800 h-12 w-6/7 pl-3 text-2xl border-blue-800 border-2' placeholder='Name' value={name} onChange={(e) => setName(e.target.value)} required /> 
              </>)}
              <button type='submit' className='bg-white font-anton rounded-md text-blue-800 hover:bg-gray-200 transition duration-300 h-12 px-10 text-4xl border-blue-800 border-2 flex justify-center items-center'   >Submit</button>
              {message && (<p className='text-blue-800 text-xl mt-2'>{message}</p> )}
            </form>
          {notFound &&(
            <div className='w-6/7 text-center'>
            <p className='text-sm'>By registering, you agree to our <button onClick={() => setType('terms')} className='hover:cursor-pointer underline'>Terms of Service</button> and <button onClick={() => setType('privacy')} className='hover:cursor-pointer underline'>Privacy Policy</button></p>
          </div>)}
        </div>
      </motion.div>
      <PolicyPopup type={type} setType={setType} location='email'/>
    </>
  );
}

export default EmailForm;
