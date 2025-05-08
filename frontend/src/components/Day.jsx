import React, { useState } from 'react';
import AddExercisePopup from './AddExercisePopup';
import Set from '../models/Set';

export default function Day ({ blockData, setBlockData, weekNum, dayNum, showDay, toggleDay, update }) {

    const [showAddExercisePopup, setShowAddExercisePopup] = useState(false);

    const toggleAddExercisePopup = () => {
        setShowAddExercisePopup(!showAddExercisePopup);
    }

    const handleAddSet = (exerciseIndex) => {
        blockData.weeks[weekNum].days[dayNum].exercises[exerciseIndex].sets.push(new Set());
        update();
    }

    const handleSetChange = (exerciseIndex, setIndex, field, value) => {
        const updatedBlockData = { ...blockData };
        updatedBlockData.weeks[weekNum].days[dayNum].exercises[exerciseIndex].sets[setIndex][field] = value;
        update()
    }

    const handleSetDelete = (exerciseIndex, setIndex) => {
        const updatedBlockData = { ...blockData };
        updatedBlockData.weeks[weekNum].days[dayNum].exercises[exerciseIndex].sets.splice(setIndex, 1);
        update()
    }

    console.log(blockData);
    
    return(<>
            <div className="flex flex-row w-full items-center mb-3">
                <button onClick={toggleDay} className="absolute text-blue-800 font-anton w-1/7 min-w-10 h-8 text-lg border-2 rounded-sm">Back</button>
                <p className="mx-auto text-blue-800 font-anton inline-block text-3xl">{blockData.weeks[weekNum].days[dayNum].name}</p>
            </div>
            <div className="flex flex-col w-full flex-grow items-center gap-2 pb-8">
            {blockData?.weeks[weekNum]?.days[dayNum]?.exercises?.length > 0 ? (
                blockData.weeks[weekNum].days[dayNum].exercises.map((exercise, exerciseIndex) => (
                    <div key={exerciseIndex} className="mb-6 p-4 border border-blue-800 rounded-md shadow-md w-full">
                        <h2 className="text-blue-800 font-anton text-2xl mb-2">{exercise?.name}</h2>

                        {exercise.sets?.map((set, setIndex) => (
                            <div key={setIndex} className="flex flex-row gap-4 mb-2">
                                <div>
                                    <label className="text-md font-anton">Weight:</label>
                                    <input
                                        type="number"
                                        className="ml-1 border rounded px-2 py-1 w-1/2 font-anton"
                                        value={set.weight}
                                        onChange={(e) => handleSetChange(exerciseIndex, setIndex, 'weight', e.target.value)}
                                    />
                                </div>
                                <div>
                                    <label className="text-md font-anton">Reps:</label>
                                    <input
                                        type="number"
                                        className="ml-1 border rounded px-2 py-1 w-1/2 font-anton"
                                        value={set.reps}
                                        onChange={(e) => handleSetChange(exerciseIndex, setIndex, 'reps', e.target.value)}
                                    />
                                </div>
                                <button onClick={(e) => handleSetDelete(exerciseIndex, setIndex)} className='font-anton'>X</button>
                            </div>
                        ))}

                        <button
                            onClick={() => handleAddSet(exerciseIndex)}
                            className="bg-blue-800 text-white px-4 py-1 rounded shadow-md mt-2 font-anton"
                        >
                            Add Set
                        </button>
                    </div>
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