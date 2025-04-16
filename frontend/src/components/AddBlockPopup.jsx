import React, { useState } from 'react';

export default function AddBlockPopup( {open, toggleAddBlockMenu} ) {

    if (!open) {
        return null;
    }

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30 backdrop-blur-sm">
          <div className="bg-white p-6 rounded-xl shadow-lg w-3/4 h-1/5">
            <p className='inline text-blue-900 font-anton text-2xl'>Add a training block</p>
            <button onClick={toggleAddBlockMenu} className="font-anton inline ml-8 text-gray-500 hover:text-gray-700 text-2xl ">X</button>
            <form>
                <input className='mt-3 bg-white font-anton rounded-md text-blue-900 h-1/4 w-6/7 pl-3 text-2xl border-blue-900 border-2' placeholder='Block Name' required /> 
                <button type='submit' onClick={toggleAddBlockMenu} className='mt-3 bg-white font-anton rounded-md text-blue-900 hover:bg-gray-200 transition duration-300 h-1/4 px-10 text-2xl border-blue-900 border-2'>Add</button>
            </form>
          </div>
        </div>
      );
      
}