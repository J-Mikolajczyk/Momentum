
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
    deleteExercise,
    dayLogged
}) {

    const [inputValues, setInputValues] = useState(() =>
        exercise?.sets?.map(set => ({
            weight: set.weight || '',
            reps: set.reps || '',
        })) || []
    );
    
    const [showMenu, setShowMenu] = useState(false);
    const [showSetMenu, setShowSetMenu] = useState(null);
    const [showRenamePopup, setShowRenamePopup] = useState(false);
    const menuRef = useRef(null); 
    const setMenuRefs = useRef([]); 
    const setMenuButtons = useRef([]); 

    useEffect(() => {
            setInputValues(
            exercise?.sets?.map(set => ({
                weight: set.weight || '',
                reps: set.reps || '',
            })) || []
            );

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

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (
            showSetMenu !== null &&
            setMenuRefs.current[showSetMenu] &&
            !setMenuRefs.current[showSetMenu].contains(event.target) &&
            setMenuButtons.current[showSetMenu] &&
            !setMenuButtons.current[showSetMenu].contains(event.target)
            ) {
            setShowSetMenu(null);
            }
        };

        if (showSetMenu !== null) {
            document.addEventListener('mousedown', handleClickOutside);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [showSetMenu]);

    const handleLocalChange = (setIndex, field, value) => {
        setInputValues(prev => {
            const updated = [...prev];
            updated[setIndex] = { ...updated[setIndex], [field]: value };
            return updated;
        });
    };

    const handleAddSet = () => {
        addSetToExercise(exercise.name, currentWeekIndex, currentDayIndex);
        setInputValues(prev => [...prev, { weight: '', reps: '' }]);
        setShowMenu(false);
    };

    const handleSetDelete = (setIndexToDelete) => {
        deleteSetFromExercise(exercise.name, setIndexToDelete, currentWeekIndex, currentDayIndex);
        const newInputValues = [...inputValues];
        newInputValues.splice(setIndexToDelete, 1);
        setShowSetMenu(null);
        setInputValues(newInputValues);
    };

    const toggleMenu = () => {
        setShowMenu(!showMenu);
    };

    const toggleSetMenu =(index) => {
        if (showSetMenu === index) {
            setShowSetMenu(null);
        } else {
            setShowSetMenu(index);
        }
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
            <div className="pl-1 pr-3 py-1.5 border border-blue-800 rounded-md shadow-md w-full">
                <div className="w-full flex flex-row justify-between items-center">
                    <h2 className="ml-3 text-blue-800 font-anton text-2xl ">{exercise?.name}</h2>
                    <div ref={menuRef} className="relative w-1/10 mb-2" >
                        {dayLogged ? null:<button
                            onClick={toggleMenu}
                            className="text-blue-800 font-anton-bold text-2xl w-full text-center cursor-pointer"
                        >⫶</button>}
                        {showMenu && (
                            <div className="absolute right-0 mt-1 w-32 bg-white border border-gray-300 rounded shadow-md z-10">
                                <button onClick={() => handleAddSet()} className="block w-full font-anton text-left px-4 py-2 hover:bg-gray-100 cursor-pointer">Add Set</button>
                                <button onClick={() => handleMove('up')} className="block w-full font-anton text-left px-4 py-2 hover:bg-gray-100 cursor-pointer">Move Up</button>
                                <button onClick={() => handleMove('down')} className="block w-full font-anton text-left px-4 py-2 hover:bg-gray-100 cursor-pointer">Move Down</button>
                                <button onClick={() => handleRename(exercise.name)} className="block w-full font-anton text-left px-4 py-2 hover:bg-gray-100 cursor-pointer">Rename</button>
                                <button onClick={() => handleDelete()} className="block w-full font-anton text-left px-4 py-2 hover:bg-gray-100 cursor-pointer">Delete</button>
                            </div>
                        )}
                    </div>
                </div>

                <div className='flex flex-col w-full'>
                <div className="flex flex-row mb-2 w-full h-full items-center gap-1">
                    <div className='w-1/20 h-full flex items-center justify-between'></div>
                    <div className='w-19/20 h-full flex items-center justify-between'>
                        <label className="text-left w-1/3 font-anton h-full text-lg">Weight:</label>
                        <label className="text-left w-1/3 font-anton h-full text-lg">Reps:</label>
                        <label className="text-left font-anton h-full text-lg w-8">Log:</label>
                    </div>
                </div>
                

                {exercise?.sets?.map((set, setIndex) => (
                    <div key={setIndex} className="relative flex flex-row mb-2 w-full items-center gap-1">
                            <button
                                    ref={(el) => (setMenuButtons.current[setIndex] = el)}
                                    onClick={() => toggleSetMenu(setIndex)} 
                                    className={`text-xl font-anton-bold pb-1 h-full w-1/20 ${set.logged ? '' : 'cursor-pointer'}`}
                                    disabled={set.logged}> {!set.logged ? '⫶' : ''}</button>
                                    {showSetMenu === setIndex && (
                                        <div className="absolute left-1 mt-15 w-25 bg-white border border-gray-300 rounded shadow-md z-4"
                                                ref={(el) => (setMenuRefs.current[setIndex] = el)}>
                                            <button onClick={() => handleSetDelete(setIndex)} className="block w-full font-anton text-left px-4 py-2 hover:bg-gray-100 cursor-pointer">Delete Set</button>
                                        </div>
                                    )}
                            <div key={setIndex} className="flex flex-row w-19/20 h-full justify-between items-center gap-1" >
                            
                            <input
                                type="number"
                                inputMode="decimal"
                                pattern="[0-9]*(\.[0-9]*)?"
                                placeholder={set.logged ? '' : (priorExercise?.sets?.[setIndex]?.weight || '')}
                                className={`text-center border rounded w-5/12 font-anton h-full text-lg text-black ${set.logged ? 'bg-gray-300' : ''}`}
                                value={inputValues[setIndex]?.weight || ''}
                                onChange={(e) => handleLocalChange(setIndex, 'weight', e.target.value)}
                                disabled={set.logged}
                            />
                            <input
                                type="number"
                                inputMode="decimal"
                                pattern="[0-9]*"
                                placeholder={set.logged ? '' : (priorExercise?.sets?.[setIndex]?.reps || '')}
                                className={`text-center border rounded w-5/12 font-anton h-full text-lg text-black ${set.logged ? 'bg-gray-300' : ''}`}
                                value={inputValues[setIndex]?.reps || ''}
                                onChange={(e) => handleLocalChange(setIndex, 'reps', e.target.value)}
                                disabled={set.logged}
                            />
                        
                            <input
                                type="checkbox"
                                checked={set.logged}
                                onChange={async () => {
                                    const { weight, reps } = inputValues[setIndex];
                                    await updateSetData(exercise.name, setIndex, weight, reps, currentWeekIndex, currentDayIndex);
                                }}
                                className={`flex items-center justify-center cursor-pointer appearance-none w-8 h-8 rounded-md border border-black
                                        checked:bg-blue-800 checked:border-blue-800
                                        relative transition-colors duration-200
                                        before:content-['✓'] before:text-3xl before:text-white before:hidden
                                        checked:before:block disabled:cursor-default`}
                                        title={set.logged ? 'Click to unlog' : 'Click to log'}
                                disabled={dayLogged}
                                 />
                        </div>
                    </div>  
                ))}
                </div>
            </div>
        </>
    );
}
