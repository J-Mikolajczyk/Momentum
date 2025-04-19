import React, { useState } from 'react';
import { postRequest } from '../utils/api';

export default function Sidebar( {open, toggleSidebar, userInfo, setUserInfo} ) {

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
        <div className="fixed bottom-0 left-0 right-0 z-50 bg-black/30 backdrop-blur-sm flex items-center justify-end bg-black/30 backdrop-blur-sm h-11/12" onClick={handleOuterClick}>
          <div className="bg-white p-6 shadow-lg w-5/6 h-full" onClick={handleInnerClick}>
            
          </div>
        </div>
      );
      
}