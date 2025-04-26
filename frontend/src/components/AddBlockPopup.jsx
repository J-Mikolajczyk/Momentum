import React, { useState, useEffect } from 'react';
import { postRequest } from '../utils/api';

export default function AddBlockPopup( {open, toggleAddBlockMenu, userInfo, setUserInfo} ) {

  const ip = import.meta.env.VITE_IP_ADDRESS;
    const [blockName, setBlockName] = useState('');
    const [message, setMessage] = useState('');

    const userId = userInfo?.id ? userInfo.id.toString() : null;
    const email = userInfo?.email != null ? userInfo.email : null;

    const handleClose = () => {
      toggleAddBlockMenu();
      setBlockName('');
      setMessage('');
    }

    const handleOuterClick = () => {
      handleClose();
    };
  
    const handleInnerClick = (e) => {
      e.stopPropagation();
    };

    const handleSubmit = async (e) => {
      if (blockName === '' || blockName === null) {
        setMessage('Block Name is required.')
        return null;
      }
      e.preventDefault();

      try {
              const response = await postRequest(ip+'/block/create', { blockName, userId });
              if (response.ok) {
                try {
                  const refreshResponse = await postRequest(ip+'/user/refresh', { email });
                  if(refreshResponse.ok) {
                    const json = await refreshResponse.json();
                    setUserInfo(json);
                    handleClose();
                  } else {
                    setMessage('Error refreshing user info')
                    return null;
                  }
                } catch (err) {
                  setMessage(err)
                  return null;
                }
                toggleAddBlockMenu();
                return null;
              } else {
                setMessage('Error creating block')
                return null;
              }
            } catch (err) {
              setMessage('Error creating block')
              return null;
            }
    }


    if (!open) {
        return <></>;
    }

    return (
        <div onClick={handleOuterClick} className="fixed inset-0 z-50 flex items-center justify-center backdrop-blur-[4.5px]">
          <div onClick={handleInnerClick} className="bg-white p-5 pr-7 rounded-xl shadow-lg w-3/4 min-h-1/4 border-blue-900 border-1">
            <div className='flex justify-between'>
            <p className='inline text-blue-900 font-anton text-2xl'>Add a training block</p>
            <button onClick={handleClose} className="font-anton inlinetext-gray-500 hover:text-gray-700 text-2xl ">X</button>
            </div>
            <form>
                <input className='mt-3 bg-white font-anton rounded-md text-blue-900 h-1/4 w-6/7 text-2xl border-blue-900 border-2 pl-3' placeholder='Block Name' value={blockName} onChange={(e) => setBlockName(e.target.value)} required /> 
                <button type='submit' onClick={handleSubmit} className='mt-3 bg-white font-anton rounded-md text-blue-900 hover:bg-gray-200 transition duration-300 h-1/4 px-10 text-2xl border-blue-900 border-2'>Add</button>
                {message && (<p className='font-anton text-red-700 text-xl mt-2'>{message}</p> )}
            </form>
            
          </div>
        </div>
      );
      
}