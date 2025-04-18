import React, { useState } from 'react';
import { postRequest } from '../utils/api';

export default function Sidebar( {open, toggleSidebar, userInfo, setUserInfo} ) {
    const [blockName, setBlockName] = useState('');
    const [message, setMessage] = useState('');

    const userId = userInfo?.id ? userInfo.id.toString() : null;
    const email = userInfo?.email != null ? userInfo.email : null;


    if (!open) {
        return null;
    }

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-end bg-black/30 backdrop-blur-sm">
          <div className="bg-white p-6 shadow-lg w-5/6 h-full">
            <div className='flex justify-between'>
                <p className='inline text-blue-900 font-anton text-4xl'>MOMENTUM</p>
                <button onClick={toggleSidebar} className="font-anton inline text-gray-500 hover:text-gray-700 text-4xl ">X</button>
            </div>
          </div>
        </div>
      );
      
}