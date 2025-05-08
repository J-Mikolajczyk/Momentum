import React, { useState, useEffect } from 'react';
import Exercise from '../models/Exercise';
import { postRequest, getRequest } from '../utils/api';

export default function AddExercisePopup( {show, toggle, blockData, index, weekNum, update} ) {

    if (!show) {
        return <></>;
    }

    const [exerciseName, setExerciseName] = useState('');
    const [message, setMessage] = useState('');

    const addExercise = async (e) => {
      e.preventDefault();
      blockData.weeks[weekNum].days[index].exercises.push(new Exercise(exerciseName));
      update();
      handleClose();
    }


    const handleClose = () => {
        toggle();
        setMessage('');
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
          <div onClick={handleInnerClick} className="bg-white p-5 pr-7 rounded-xl shadow-lg w-3/4 min-h-1/4">
            <div className='flex justify-between'>
            <p className='inline text-blue-900 font-anton text-2xl'>Add an exercise</p>
            <button onClick={handleClose} className="font-anton inlinetext-gray-500 hover:text-gray-700 text-2xl ">X</button>
            </div>
            <form>
                <input className='mt-3 bg-white font-anton rounded-md text-blue-900 h-1/4 w-6/7 text-2xl border-blue-900 border-2 pl-3' placeholder='Exercise Name' value={exerciseName} onChange={(e) => setExerciseName(e.target.value)} required /> 
                <button type='submit' onClick={addExercise} className='mt-3 bg-white font-anton rounded-md text-blue-900 hover:bg-gray-200 transition duration-300 h-1/4 px-10 text-2xl border-blue-900 border-2'>Add</button>
                {message && (<p className='font-anton text-red-700 text-xl mt-2'>{message}</p> )}
            </form>
            
          </div>
        </div>
      );
      
}