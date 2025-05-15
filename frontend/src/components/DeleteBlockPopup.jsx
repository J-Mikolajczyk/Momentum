import React, { useState, useEffect } from 'react';
import setThemeColor from '../hooks/useThemeColor'

export default function DeleteBlockPopup( { show, toggle, name, deleteMethod } ) {

    if (!show) {
        return <></>;
    }

    const [newName, setNewName] = useState('');

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
          <div onClick={handleInnerClick} className="bg-white p-5 pr-7 rounded-xl shadow-lg w-3/4 min-h-1/5">
            <div className='flex flex-col justify-between'>
            <p className='text-blue-800 font-anton text-2xl'>Are you sure you would like to delete {name}?</p>
            <div className='flex flex-row w-full justify-around'>
              <button type='submit' onClick={() => {handleClose(); deleteMethod(name);}} className='mt-3 bg-white font-anton rounded-md text-blue-800 px-10 text-xl border-blue-800 border-2 w-1/3 text-center flex items-center justify-center h-10'>Confirm</button>
              <button type='submit' onClick={handleClose} className='mt-3 bg-white font-anton rounded-md text-blue-800 px-10 text-xl border-blue-800 border-2 w-1/3 text-center flex items-center justify-center h-10'>Cancel</button>     </div>
            </div>
          </div>
        </div>
      );
      
}