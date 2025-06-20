import React, { useState, useEffect, use } from 'react';
import setThemeColor from '../../hooks/useThemeColor'
import { useUser } from '../../contexts/UserContext';
import { deleteRequest } from '../../utils/api';

const ip = import.meta.env.VITE_IP_ADDRESS;

export default function DeleteAccountPopup( { show, toggle } ) {

    if (!show) {
        return <></>;
    }

    const { logOut, userInfo } = useUser();
    const [ password, setPassword ] = useState('');
    const [ message, setMessage ] = useState('');
    const email = userInfo?.email;

    useEffect(() => {
        setThemeColor('#0D1E5C'); 
    }, []);

    const handleClose = () => {
        toggle();
        setThemeColor('#193cb8')
    }

    const handleOuterClick = () => {
      handleClose();
    };
  
    const handleInnerClick = (e) => {
      e.stopPropagation();
    };

    const confirm = async () => {
      try {
        const response = await deleteRequest({ email, password });

        if (response.status === 401) {
          setMessage("Incorrect password");
        }

        if(response.ok) {
          const data = await response.json();
          if (data.deleted === true) {
            toggle();
            logOut();
          }
        }
      } catch (err) {
        setMessage(err);
      }
    };

    if (!open) {
        return <></>;
    }

    return (
        <div onClick={handleOuterClick} className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <form onClick={handleInnerClick} onSubmit={(e) => {e.preventDefault(); confirm();}} className="bg-white py-2 px-4 rounded-xl shadow-lg w-3/4 min-h-1/5 min-w-50 max-w-90 flex flex-col items-center justify-around gap-2">
            <p className="font-anton text-red-700 text-xl mt-2 text-center"> Are you sure you would like to delete your account?</p>
            <input className="bg-white font-anton rounded-md text-blue-800 w-6/7 pl-3 text-xl border-blue-800 border-2 h-8" placeholder="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
            <div className="flex justify-around w-full">
              <button type="submit" className="w-1/3 bg-blue-800 font-anton text-white rounded-md hover:bg-blue-600 flex items-center justify-center min-w-12 pr-1 cursor-pointer" > Confirm </button>
              <button type="button" onClick={handleClose} className="w-1/3 bg-gray-500 font-anton text-white rounded-md hover:bg-gray-600 flex items-center justify-center pr-1 min-w-12 cursor-pointer">Cancel</button>
            </div>
            <p className="font-anton text-red-700 text-xl mt-2 text-center">{message}</p>       
        </form>
      </div>
    );
      
}