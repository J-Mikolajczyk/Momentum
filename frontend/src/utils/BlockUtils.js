import { postRequest } from './api';

export async function updateBlock(blockData, blockName, ip, refreshCallback) {
  const { id, weeks } = blockData;

  try {
    const response = await postRequest(`${ip}/secure/block/update`, { id, name: blockName, weeks });
    if (response.ok) {
      if (refreshCallback) {
        refreshCallback();
      }
    } else {
      console.error('Error updating block');
    }
  } catch (error) {
    console.error('Update failed:', error);
  }
}