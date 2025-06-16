import React, { useState, useEffect } from 'react';
import setThemeColor from '../../hooks/useThemeColor'

export default function DeleteBlockPopup( { show, toggle, name, deleteMethod } ) {

    if (!show) {
        return <></>;
    }

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

    if (!open) {
        return <></>;
    }

    return (
        <div onClick={handleOuterClick} className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div onClick={handleInnerClick} className="bg-white p-2 rounded-xl shadow-lg w-3/4 min-h-1/6 min-w-50 max-w-90 flex flex-col items-center justify-around gap-2">
            <p className="font-anton text-red-700 text-xl mt-2 text-center">Are you sure you would like to delete {name}?</p>
            <div className="flex justify-around w-full">
              <button type='submit' onClick={() => {handleClose(); deleteMethod(name);}} className="w-1/3 bg-blue-800 font-anton text-white rounded-md hover:bg-blue-600 flex items-center justify-center min-w-12 pr-1 cursor-pointer">Confirm</button>
              <button type='submit' onClick={handleClose} className="w-1/3 bg-gray-500 font-anton text-white rounded-md hover:bg-gray-600 flex items-center justify-center pr-1 min-w-12 cursor-pointer">Cancel</button>     </div>
            </div>
        </div>
      );
      
}