import React from 'react';

export default function MessagePopup({ message, setMessage, proceedToAddWeek, onCancel }) {
  if (!message) {
    return null;
  }

  const handleClose = () => {
    setMessage(null);
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
        <div className="flex justify-between gap-4 mt-4 mx-6">
            <button onClick={() => { proceedToAddWeek(); handleClose(); }}  className="bg-blue-500 font-anton text-white px-4 py-2 rounded-md hover:bg-blue-600"> Ignore</button>
            <button onClick={() => { handleClose(); }} className="bg-gray-500 font-anton text-white px-4 py-2 rounded-md hover:bg-gray-600" >Cancel </button>
        </div>
      </div>
    </div>
  );
}