import React, { useState } from 'react';
import AddExercisePopup from './AddExercisePopup';
import ExerciseCard from './ExerciseCard'


export default function Day ({ blockData, weekNum, dayNum, update, setDayIndex }) {

    const [showAddExercisePopup, setShowAddExercisePopup] = useState(false);

    const toggleAddExercisePopup = () => {
        setShowAddExercisePopup(!showAddExercisePopup);
    }
    
    return(<>
            <div className="flex flex-col w-full flex-grow items-center gap-2 pb-8">
            {blockData?.weeks[weekNum]?.days[dayNum]?.exercises?.length > 0 ? (
                blockData?.weeks[weekNum].days[dayNum].exercises.map((exercise, exerciseIndex) => (
                    <ExerciseCard weekNum={weekNum} dayNum={dayNum} blockData={blockData} key={exerciseIndex} exercise={exercise} update={update} exerciseIndex={exerciseIndex}/>
                ))
            ) : (
                <div className="flex flex-row w-full items-center justify-between">
                    <p className="text-gray-500 font-anton text-2xl">No Exercises Added</p>
                </div>
            )}


            <button onClick={toggleAddExercisePopup} className="flex elect-none bg-gray-400 text-gray-500 font-anton w-1/4 min-w-28 h-8 text-lg border items-center justify-center border-gray-500 rounded-xs ml-auto">Add Exercise</button>
            
            <AddExercisePopup show={showAddExercisePopup} toggle={toggleAddExercisePopup} blockData={blockData} index={dayNum} weekNum={weekNum} update={update}/>
                         
            </div>
            </>
        )
}