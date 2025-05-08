import { useState, useEffect, useRef } from 'react';
import AddDayPopup from './AddDayPopup';
import RenameDayPopup from './RenameDayPopup';
import RenameDeleteMenu from './RenameDeleteMenu'

export default function DayDashboard({ blockData, openDay, weekNum, setBlockData, blockName, update }) {
    const [showAddDayPopup, setShowAddDayPopup] = useState(false);
    const [showRenameDayPopup, setShowRenameDayPopup] = useState(false);
    const [renameIndex, setRenameIndex] = useState(null);
    const [dropdownIndex, setDropdownIndex] = useState(null);
    const dropdownRef = useRef(null);

    const toggleAddDayPopup = () => setShowAddDayPopup(!showAddDayPopup);

    const toggleRenameDayPopup = (index) => {
        setRenameIndex(index);
        setShowRenameDayPopup(!showRenameDayPopup);
    };

    const toggleDropdown = (index) => {
        setDropdownIndex(prev => (prev === index ? null : index));
    };

    const deleteDay = (index) => {
        const updatedBlockData = { ...blockData };
        updatedBlockData.weeks[weekNum - 1].days.splice(index, 1);
        update();
        toggleDropdown();
    };

    useEffect(() => {
        function handleClickOutside(event) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setDropdownIndex(null);
            }
        }
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    return <>
        <AddDayPopup show={showAddDayPopup} toggle={toggleAddDayPopup} blockData={blockData} setBlockData={setBlockData} blockName={blockName} weekNum={weekNum} update={update} />
        <RenameDayPopup show={showRenameDayPopup} toggle={toggleRenameDayPopup} blockData={blockData} setBlockData={setBlockData} blockName={blockName} weekNum={weekNum} update={update} index={renameIndex} />

        {blockData?.weeks?.length > 0 && (
            <>
                {blockData.weeks[weekNum - 1]?.days?.length > 0 ? (
                    <>
                        {blockData.weeks[weekNum - 1].days.map((day, index) => (
                            <div key={index} className="relative flex flex-row bg-blue-50 text-blue-800 px-4 py-2 rounded-md shadow-md w-full text-2xl border-blue-800 border justify-between items-center">
                                <button onClick={() => openDay(index)} className="text-left w-3/4 font-anton">{day.name}</button>
                                <button onClick={() => toggleDropdown(index)} className="text-right w-1/5 font-anton-no-italic">⫶</button>
                                {dropdownIndex === index && (
                                    <RenameDeleteMenu renameMethod={toggleRenameDayPopup} deleteMethod={deleteDay} index={index} ref={dropdownRef} />
                                    
                                )}
                            </div>
                        ))}
                    </>
                ) : (
                    <div className="flex flex-row w-full items-center justify-between">
                        <p className="text-gray-500 font-anton text-2xl">No Days Created</p>
                    </div>
                )}
                <button onClick={toggleAddDayPopup} className="flex elect-none bg-gray-400 text-gray-500 font-anton w-1/4 min-w-21 h-6 text-l border items-center justify-center border-gray-500 rounded-xs ml-auto">Add Day</button>
            </>
        )}
    </>
}
