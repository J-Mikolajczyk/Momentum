import React, { useState } from 'react';
import Block from './Block';
import WeekMenu from './WeekMenu';
import MessagePopup from './MessagePopup';
import useBlock from '../hooks/useBlock';

export default function BlockDashboard({ blockName, setBlockName, userInfo, toggleAddBlockMenu }) {
  const {
    blockData,
    currentWeekNum,
    currentDayIndex,
    weekText,
    message,
    setMessage,
    ignoreMethod,
    setWeekAndDay,
    addWeek,
    removeWeek,
    refreshBlock,
    addExerciseToDay,
    addSetToExercise,
    deleteSetFromExercise,
    updateSetData,
  } = useBlock(blockName, userInfo);

  const name = userInfo.name;

  const openBlock = (blockName) => {
    setBlockName(blockName);
  };

  return (
    <>
      <MessagePopup message={message} setMessage={setMessage} ignoreMethod={ignoreMethod} />
      {blockName === null ? (
        <div className="p-4 w-full">
          <div className="flex w-full items-center mb-3">
            <p className="text-blue-800 font-anton inline-block text-3xl">Welcome, {name}</p>
            <button
              onClick={toggleAddBlockMenu}
              className="inline-block elect-none bg-gray-400 text-gray-500 font-anton ml-auto w-1/4 min-w-21 h-10 text-xl border border-gray-500 rounded-xs"
            >
              Add Block
            </button>
          </div>
          {userInfo?.trainingBlockNames?.length > 0 ? (
            userInfo.trainingBlockNames.map((blockName, index) => (
              <button
                onClick={() => openBlock(blockName)}
                key={index}
                className="bg-blue-50 text-blue-800 font-anton px-4 py-2 rounded-md shadow-md w-full text-left text-2xl border-blue-800 border-1 mb-1"
              >
                {blockName}
              </button>
            ))
          ) : (
            <p className="text-gray-500 font-anton text-2xl">No Training Blocks Created</p>
          )}
        </div>
      ) : (
        <>
          <WeekMenu
            blockData={blockData}
            setWeekAndDay={setWeekAndDay}
            weekText={weekText}
            addWeek={addWeek}
            removeWeek={removeWeek}
          />
          <Block blockName={blockName} userInfo={userInfo} />
        </>
      )}
    </>
  );
}
