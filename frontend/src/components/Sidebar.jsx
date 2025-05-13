import React, { useState } from 'react';
import { postRequest } from '../utils/api';

export default function Sidebar( { logOut, open, toggleSidebar, userInfo, setUserInfo} ) {

    const handleOuterClick = () => {
      toggleSidebar();
    };
  
    const handleInnerClick = (e) => {
      e.stopPropagation();
    };


    if (!open) {
        return null;
    }

    return (
        <div className="fixed bottom-0 left-0 right-0 z-50 bg-black/50 flex items-center justify-end bg-black/30 h-11/12" onClick={handleOuterClick}>
          <div className="bg-white p-6 shadow-lg w-5/6 h-full" onClick={handleInnerClick}>
              <button onClick={logOut} className='inline-block elect-none bg-gray-400 text-gray-500 font-anton ml-auto w-1/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs' >Log Out</button>
        
          </div>
        </div>
      );
      
}