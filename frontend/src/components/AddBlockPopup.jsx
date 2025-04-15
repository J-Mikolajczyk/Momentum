import React, { useState } from 'react';

export default function AddBlockPopup( {open, toggleAddBlockMenu} ) {

    if(!open){
        return (null)
    }

    return (
        <div >
            <button onClick={toggleAddBlockMenu} className='select-none bg-gray-400 text-gray-500 font-anton ml-auto w-full h-10 text-xl border-1 border-gray-500'>Close</button>   
        </div>
    )
}