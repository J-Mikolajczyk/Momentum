import React, { useState } from 'react';

export default function Block({blockName}) {
    return <>
                <p className='text-blue-800 font-anton mr-auto text-3xl'>{blockName}</p>
           </>
}