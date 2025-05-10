import { postRequest, getRequest } from './api';
import Week from '../models/Week';

const ip = import.meta.env.VITE_IP_ADDRESS;

export const addExerciseToBlock = async (blockData, exerciseName, weekNum, dayIndex) => {
    if (!exerciseName) {
        throw new Error('Exercise name is required.');
    }

    for (let i = weekNum; i < blockData.weeks.length; i++) {
        blockData.weeks[i].days[dayIndex].exercises.push(new Exercise(exerciseName));
    }

    await updateBlockData(blockData);
};

export const addWeekToBlock = async (blockData) => {
    if (blockData.weeks.length >= 6) {
        throw new Error("Mesocycles longer than 6 weeks are not recommended. Please consider a deload.");
    }

    const newWeek = blockData.weeks.length === 0 || blockData.weeks[blockData.weeks.length - 1].days.length === 0
        ? new Week()
        : new Week(blockData.weeks[blockData.weeks.length - 1].days);

    blockData.weeks.push(newWeek);
    await updateBlockData(blockData);
};

export const removeWeekFromBlock = async (blockData) => {
    if (blockData.weeks.length <= 1) {
        throw new Error("Cannot remove only week in training block.");
    }

    blockData.weeks.pop();
    await updateBlockData(blockData);
};

export const updateBlockData = async (blockData) => {
    const { id, name, weeks } = blockData;
    try {
        const response = await postRequest(`${ip}/secure/block/update`, { name, id, weeks });
        if (!response.ok) {
            throw new Error('Issue updating block');
        }
    } catch (err) {
        console.error(err);
    }
};

export const refreshBlockData = async (blockName, userId) => {
    try {
        const response = await getRequest(`${ip}/secure/block/get`, { blockName, userId });
        if (!response.ok) {
            throw new Error('Non-OK response');
        }
        return await response.json();
    } catch (err) {
        console.error(err);
    }
};