import { useEffect, useState } from 'react';

export default function ExerciseCard({ 
    exercise, 
    currentWeekIndex, 
    currentDayIndex,     
    addSetToExercise,
    deleteSetFromExercise,
    updateSetData 
}) {

    useEffect(() => {
        setInputValues(
            exercise?.sets?.map(set => ({
                weight: set.weight || '',
                reps: set.reps || '',
            })) || []
        );
    }, [exercise.sets]);

    const [inputValues, setInputValues] = useState(() => {
        return exercise?.sets?.map(set => ({
            weight: set.weight || '',
            reps: set.reps || '',
        })) || [];
    });

    const handleLocalChange = (setIndex, field, value) => {
        const updatedInputs = [...inputValues];
        updatedInputs[setIndex] = { ...updatedInputs[setIndex], [field]: value };
        setInputValues(updatedInputs);
    };

    const handleBlur = (setIndex, field) => {
        const value = inputValues[setIndex][field];
        updateSetData(exercise.name, setIndex, field, value, currentWeekIndex, currentDayIndex);
    };

    const handleAddSet = () => {
        addSetToExercise(exercise.name, currentWeekIndex, currentDayIndex);
        setInputValues(prev => [...prev, { weight: '', reps: '' }]);
      };
      
      

      const handleSetDelete = (setIndexToDelete) => {
        deleteSetFromExercise(exercise.name, setIndexToDelete, currentWeekIndex, currentDayIndex);
        const newInputValues = [...inputValues];
        newInputValues.splice(setIndexToDelete, 1);
        setInputValues(newInputValues);
      };
      
      

    return (
        <div className="p-2 pr-3 border border-blue-800 rounded-md shadow-md w-full">
            <h2 className="text-blue-800 font-anton text-2xl mb-2">{exercise?.name}</h2>

            {exercise?.sets?.map((set, setIndex) => (
                <div key={setIndex} className="flex flex-row mb-2 w-full justify-between items-center gap-2">
                    <div className=' flex items-center w-full'>
                        <label className="text-lg font-anton">Weight:</label>
                        <input
                            type="number"
                            className="flex text-center justify-center ml-1 border rounded w-full font-anton h-full text-lg pr-1"
                            value={inputValues[setIndex]?.weight || ''}
                            onChange={(e) => handleLocalChange(setIndex, 'weight', e.target.value)}
                            onBlur={() => handleBlur(setIndex, 'weight')}
                        />
                    </div>
                    <div className=' flex items-center w-full'>
                        <label className="text-lg font-anton">Reps:</label>
                        <input
                            type="number"
                            className="flex text-center justify-center ml-1 border rounded w-full font-anton h-full text-lg pr-1"
                            value={inputValues[setIndex]?.reps || ''}
                            onChange={(e) => handleLocalChange(setIndex, 'reps', e.target.value)}
                            onBlur={() => handleBlur(setIndex, 'reps')}
                        />
                    </div>
                    <button onClick={() => handleSetDelete(setIndex)} className='font-anton-no-italic'>X</button>
                 </div>
            ))}

            <button
                onClick={handleAddSet}
                className="bg-blue-800 text-white px-4 py-1 rounded shadow-md mt-2 font-anton"
            >
                Add Set
            </button>
        </div>
    );
}
