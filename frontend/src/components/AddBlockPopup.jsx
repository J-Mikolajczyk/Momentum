import React, { useState } from 'react';
import { postRequest } from '../utils/api';

export default function AddBlockPopup( {open, toggleAddBlockMenu, userInfo, setUserInfo} ) {
    const [blockName, setBlockName] = useState('');
    const [message, setMessage] = useState('');

    const userId = userInfo?.id ? userInfo.id.toString() : null;
    const email = userInfo?.email != null ? userInfo.email : null;

    const handleSubmit = async (e) => {
      e.preventDefault();

      try {
              const response = await postRequest('http://localhost:8080/block/create', { blockName, userId });
              if (response.ok) {
                try {
                  const refreshResponse = await postRequest('http://localhost:8080/user/refresh', { email });
                  if(refreshResponse.ok) {
                    const json = await refreshResponse.json();
                    setUserInfo(json);
                    setMessage('')
                    setBlockName('');
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
        return null;
    }

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm">
          <div className="bg-white p-6 rounded-xl shadow-lg w-3/4 h-1/4">
            <p className='inline text-blue-900 font-anton text-2xl'>Add a training block</p>
            <button onClick={toggleAddBlockMenu} className="font-anton inline ml-8 text-gray-500 hover:text-gray-700 text-2xl ">X</button>
            <form>
                <input className='mt-3 bg-white font-anton rounded-md text-blue-900 h-1/4 w-6/7 pl-3 text-2xl border-blue-900 border-2' placeholder='Block Name' value={blockName} onChange={(e) => setBlockName(e.target.value)} required /> 
                <button type='submit' onClick={handleSubmit} className='mt-3 bg-white font-anton rounded-md text-blue-900 hover:bg-gray-200 transition duration-300 h-1/4 px-10 text-2xl border-blue-900 border-2'>Add</button>
                {message && (<p className='font-anton text-red-700 text-xl mt-2'>{message}</p> )}
            </form>
            
          </div>
        </div>
      );
      
}