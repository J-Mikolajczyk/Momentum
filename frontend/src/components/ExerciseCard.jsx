import { useEffect, useRef, useState } from 'react';
import RenamePopup from './RenamePopup';

export default function ExerciseCard({
    exercise,
    priorExercise,
    currentWeekIndex,
    currentDayIndex,
    addSetToExercise,
    deleteSetFromExercise,
    updateSetData,
    exerciseIndex,
    moveExercise,
    renameExercise,
    deleteExercise
}) {
    const [dirtyFields, setDirtyFields] = useState(() => {
        return exercise?.sets?.map(() => ({ weight: false, reps: false })) || [];
    });
    const [inputValues, setInputValues] = useState(() => {
        return exercise?.sets?.map(set => ({
            weight: set.weight || '',
            reps: set.reps || '',
        })) || [];
    });
    const [showMenu, setShowMenu] = useState(false);
    const [showRenamePopup, setShowRenamePopup] = useState(false);
    const menuRef = useRef(null); 

    useEffect(() => {
        const anyFieldDirty = dirtyFields.some(fields => fields.weight || fields.reps);
        if (!anyFieldDirty) {
            setInputValues(
                exercise?.sets?.map(set => ({
                    weight: set.weight || '',
                    reps: set.reps || '',
                })) || []
            );
        }
    }, [exercise.sets]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setShowMenu(false);
            }
        };

        if (showMenu) {
            document.addEventListener('mousedown', handleClickOutside);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [showMenu]);

    const handleFocus = (setIndex, field) => {
        setDirtyFields(prev => {
            const updated = [...prev];
            updated[setIndex] = { ...updated[setIndex], [field]: true };
            return updated;
        });
    };

    const handleLocalChange = (setIndex, field, value) => {
        const updatedInputs = [...inputValues];
        updatedInputs[setIndex] = { ...updatedInputs[setIndex], [field]: value };
        setInputValues(updatedInputs);
    };

    const handleBlur = (setIndex, field) => {
        const value = inputValues[setIndex][field];
        updateSetData(exercise.name, setIndex, field, value, currentWeekIndex, currentDayIndex);

        setDirtyFields(prev => {
            const updated = [...prev];
            updated[setIndex] = { ...updated[setIndex], [field]: false };
            return updated;
        });
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

    const toggleMenu = () => {
        setShowMenu(!showMenu);
    };

    const handleMove = (direction) => {
        moveExercise(direction, exerciseIndex);
        setShowMenu(false);
    };

    const handleRename = () => {
        setShowRenamePopup(true);
        setShowMenu(false);
    };

    const handleDelete = () => {
        deleteExercise(exercise.name);
        setShowMenu(false);
    };

    return (
        <>
            <RenamePopup show={showRenamePopup} toggle={() => setShowRenamePopup()} name={exercise.name} rename={renameExercise}  />
            <div className="p-2.5 border border-blue-800 rounded-md shadow-md w-full">
                <div className="w-full flex flex-row justify-between items-center">
                    <h2 className="text-blue-800 font-anton text-2xl ">{exercise?.name}</h2>
                    <div ref={menuRef} className="relative w-1/10 mb-2">
                        <button
                            onClick={toggleMenu}
                            className="text-blue-800 font-anton-bold text-2xl w-full text-center cursor-pointer"
                        >
                            ⫶
                        </button>
                        {showMenu && (
                            <div className="absolute right-0 mt-1 w-32 bg-white border border-gray-300 rounded shadow-md z-10">
                                <button onClick={() => handleMove('up')} className="block w-full font-anton text-left px-4 py-2 hover:bg-gray-100 cursor-pointer">Move Up</button>
                                <button onClick={() => handleMove('down')} className="block w-full font-anton text-left px-4 py-2 hover:bg-gray-100 cursor-pointer">Move Down</button>
                                <button onClick={() => handleRename(exercise.name)} className="block w-full font-anton text-left px-4 py-2 hover:bg-gray-100 cursor-pointer">Rename</button>
                                <button onClick={() => handleDelete()} className="block w-full font-anton text-left px-4 py-2 hover:bg-gray-100 cursor-pointer">Delete</button>
                            </div>
                        )}
                    </div>
                </div>

                {exercise?.sets?.map((set, setIndex) => (
                    <div key={setIndex} className="flex flex-row mb-2 w-full justify-between items-center gap-2">
                        <div className="flex items-center w-full">
                            <label className="text-lg font-anton mr-1">Weight:</label>
                            <input
                                type="number"
                                inputMode="decimal"
                                pattern="[0-9]*(\.[0-9]*)?"
                                placeholder={priorExercise?.sets?.[setIndex]?.weight || ''}
                                className="text-center border rounded w-full font-anton h-full text-lg"
                                value={inputValues[setIndex]?.weight || ''}
                                onFocus={() => handleFocus(setIndex, 'weight')}
                                onChange={(e) => handleLocalChange(setIndex, 'weight', e.target.value)}
                                onBlur={() => handleBlur(setIndex, 'weight')}
                            />
                        </div>
                        <div className="flex items-center w-full">
                            <label className="text-lg font-anton mr-1">Reps:</label>
                            <input
                                type="number"
                                inputMode="decimal"
                                pattern="[0-9]*(\.[0-9]*)?"
                                placeholder={priorExercise?.sets?.[setIndex]?.reps || ''}
                                className="text-center border rounded w-full font-anton h-full text-lg"
                                value={inputValues[setIndex]?.reps || ''}
                                onFocus={() => handleFocus(setIndex, 'reps')}
                                onChange={(e) => handleLocalChange(setIndex, 'reps', e.target.value)}
                                onBlur={() => handleBlur(setIndex, 'reps')}
                            />
                        </div>
                        <button onClick={() => handleSetDelete(setIndex)} className="font-anton cursor-pointer">X</button>
                    </div>
                ))}

                <button onClick={handleAddSet} className="bg-blue-800 text-white px-4 py-1 rounded shadow-md mt-2 font-anton cursor-pointer">Add Set</button>
            </div>
        </>
    );
}
