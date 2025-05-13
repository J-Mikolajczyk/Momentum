import React, { useEffect } from 'react';
import setThemeColor from '../hooks/useThemeColor'

export default function MessagePopup({ message, setMessage, ignoreMethod, setIgnoreMethod }) {
  if (!message) {
    return null;
  }

  useEffect(() => {
          setThemeColor('#0D1E5C'); 
  }, []);

  const handleClose = () => {
    setMessage(null);
    setIgnoreMethod(null);
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
      <div onClick={handleInnerClick} className="bg-white p-2 rounded-xl shadow-lg w-3/4 min-h-1/6 min-w-50 flex flex-col items-center justify-center gap-2">
        {message && (
          <p className="font-anton text-red-700 text-xl mt-2 text-center">{message}</p>
        )}
        <div className="flex justify-around w-full">
          { ignoreMethod !== null ? 
            (<button onClick={() => { ignoreMethod(); handleClose(); }}  className="w-1/3 bg-blue-800 font-anton text-white rounded-md hover:bg-blue-600 flex items-center justify-center min-w-12 pr-1 cursor-pointer"> Ignore</button>) : (<></>)}
          <button onClick={() => { handleClose(); }} className="w-1/3 bg-gray-500 font-anton text-white rounded-md hover:bg-gray-600 flex items-center justify-center pr-1 min-w-12 cursor-pointer" >Cancel </button>
            (<button onClick={() => { console.log(ignoreMethod); ignoreMethod(); handleClose(); }}  className="w-1/3 bg-blue-800 font-anton text-white rounded-md hover:bg-blue-600 flex items-center justify-center min-w-12 pr-1"> Ignore</button>) : (<></>)
          <button onClick={() => { handleClose(); }} className="w-1/3 bg-gray-500 font-anton text-white rounded-md hover:bg-gray-600 flex items-center justify-center pr-1 min-w-12" >Cancel </button>
        </div>
      </div>
    </div>
  );
}