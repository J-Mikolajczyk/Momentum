import React, { useEffect } from 'react';
import setThemeColor from '../hooks/useThemeColor'

export default function MessagePopup({ message, setMessage, ignoreMethod }) {
  if (!message) {
    return null;
  }

  useEffect(() => {
          setThemeColor('#0D1E5C'); 
  }, []);

  const handleClose = () => {
    setMessage(null);
    setThemeColor('#193cb8')
  };

  const handleOuterClick = () => {
    handleClose();
  };

  const handleInnerClick = (e) => {
    e.stopPropagation();
  };

  return (
    <div onClick={handleOuterClick} className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div onClick={handleInnerClick} className="bg-white p-5 pr-7 rounded-xl shadow-lg w-3/4 min-h-1/6">
        {message && (
          <p className="font-anton text-red-700 text-xl mt-2 text-center">{message}</p>
        )}
        <div className="flex justify-around gap-4 mt-4 mx-6">
          { ignoreMethod !== null ? 
            (<button onClick={() => { ignoreMethod(); handleClose(); }}  className="bg-blue-800 font-anton text-white px-4 py-2 rounded-md hover:bg-blue-600"> Ignore</button>) : (<></>)}
          <button onClick={() => { handleClose(); }} className="bg-gray-500 font-anton text-white px-4 py-2 rounded-md hover:bg-gray-600" >Cancel </button>
        </div>
      </div>
    </div>
  );
}