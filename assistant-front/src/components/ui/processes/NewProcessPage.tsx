import { Link, useLocation } from "react-router-dom";
import { IoIosCheckboxOutline } from "react-icons/io";
import { CgShapeRhombus } from "react-icons/cg";
import { IoExitOutline, IoSaveOutline } from 'react-icons/io5';
import { FaRegSquare, FaCode } from "react-icons/fa";
import { GrCursor } from "react-icons/gr";
import { BsArrowUpLeft } from "react-icons/bs";
import { ResizableHandle, ResizablePanel, ResizablePanelGroup } from "@/components/ui/shadcn/resizable";
import { useState } from 'react';
import Draggable from 'react-draggable';
import Block from "../blocks/Block";
import CheckBlock from "../blocks/CheckBlock";
import Predicate from "../blocks/Predicate";

const NewProcessPage: React.FC = () => {
  const location = useLocation();
  const [activeView, setActiveView] = useState<'blocks' | 'code'>('blocks');
  const [selectedTool, setSelectedTool] = useState<string>('cursor'); 
  const [blocks, setBlocks] = useState<{ x: number; y: number }[]>([]);   

  const getActiveClass = (path: string) =>  
    location.pathname === path ? "bg-green-500 text-white" : "text-gray-700";

  const handleViewChange = (view: 'blocks' | 'code') => {
    setActiveView(view);
  };

  const handleToolSelect = (tool: string) => {
    setSelectedTool(tool);
  };

  const handlePanelClick = (e: React.MouseEvent) => {
    if (selectedTool === 'block') {
      const rect = e.currentTarget.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;

      setBlocks((prevBlocks) => [...prevBlocks, { x, y }]);
    }
  };

  return (
    <div>
      <div className="flex justify-around p-4 bg-gray-500">
        <div className="flex gap-4 p-2 bg-gray-100 rounded-full shadow-md">
          <Link to="/createprocess">
            <button className={`px-4 py-2 rounded-full transition-all duration-300 ${getActiveClass("/createprocess")}`}>
              <IoSaveOutline size={25} />
            </button>
          </Link>
          <input
            type="text"
            placeholder="Untitled..."
            className="flex-grow mx-2 px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-green-500"
          />
          <Link to="/createprocess">
            <button className={`px-4 py-2 rounded-full transition-all duration-300 ${getActiveClass("/createprocess")}`}>
              <IoExitOutline size={25} />
            </button>
          </Link>
        </div>
      </div>

      <ResizablePanelGroup direction="horizontal" className="min-h-screen rounded-lg border md:min-w-[450px]">
        
        <ResizablePanel defaultSize={25} minSize={20}>
            <div className="flex justify-around p-2 bg-gray-200">
                <div className="flex gap-4 p-2 bg-gray-100 rounded-full shadow-md">
                    <button
                        onClick={() => handleViewChange('blocks')}
                        className={`px-4 py-2 rounded-full transition-all duration-100 ${activeView === 'blocks' ? 'bg-green-500 text-white' : 'text-gray-700'}`}
                    >
                        <FaRegSquare size={15} />
                    </button>
                    <button
                        onClick={() => handleViewChange('code')}
                        className={`px-4 py-2 rounded-full transition-all duration-100 ${activeView === 'code' ? 'bg-green-500 text-white' : 'text-gray-700'}`}
                    >
                        <FaCode size={15} />
                    </button>
                </div>
            </div>

          <div className="p-4">
            {activeView === 'blocks' ? (
              <div className="">
                <div className="grid mb-7 border-b-2 pb-7 grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-3 gap-6">
                  <div
                    className={`flex flex-col items-center justify-center w-20 h-20 rounded-lg shadow-md transition ${selectedTool === 'cursor' ? 'bg-gray-300' : 'bg-gray-100 hover:bg-gray-200'}`}
                    onClick={() => handleToolSelect('cursor')}
                  >
                    <GrCursor className="text-gray-500 text-4xl mt-4 size-7" />
                    <p className="text-md font-semibold text-gray-600">Cursor</p>
                  </div>
                  <div
                    className={`flex flex-col items-center justify-center w-20 h-20 rounded-lg shadow-md transition ${selectedTool === 'arrow' ? 'bg-gray-300' : 'bg-gray-100 hover:bg-gray-200'}`}
                    onClick={() => handleToolSelect('arrow')}
                  >
                    <BsArrowUpLeft className="text-gray-500 text-4xl mt-4 size-8" />
                    <p className="text-sm font-semibold text-gray-600">Arrow</p>
                  </div>
                </div>
                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-3 gap-6">
                  <div
                    className={`flex flex-col items-center justify-center w-20 h-20 rounded-lg shadow-md transition ${selectedTool === 'block' ? 'bg-gray-300' : 'bg-gray-100 hover:bg-gray-200'}`}
                    onClick={() => handleToolSelect('block')}
                  >
                    <FaRegSquare className="text-gray-500 text-4xl mt-4 size-7" />
                    <p className="text-md font-semibold text-gray-600">Block</p>
                  </div>
                  <div
                    className={`flex flex-col items-center justify-center w-20 h-20 rounded-lg shadow-md transition ${selectedTool === 'checkBlock' ? 'bg-gray-300' : 'bg-gray-100 hover:bg-gray-200'}`}
                    onClick={() => handleToolSelect('checkBlock')}
                  >
                    <IoIosCheckboxOutline className="text-gray-500 text-4xl mt-4 size-8" />
                    <p className="text-sm font-semibold text-gray-600">Check Block</p>
                  </div>
                  <div
                    className={`flex flex-col items-center justify-center w-20 h-20 rounded-lg shadow-md transition ${selectedTool === 'predicate' ? 'bg-gray-300' : 'bg-gray-100 hover:bg-gray-200'}`}
                    onClick={() => handleToolSelect('predicate')}
                  >
                    <CgShapeRhombus className="text-gray-500 text-4xl mt-4 size-7" />
                    <p className="text-md font-semibold text-gray-600">Predicate</p>
                  </div>
                </div>
              </div>
            ) : (
              <div className="bg-gray-100 p-4 rounded-md shadow-inner">
                <div className="flex">
                  <div className="bg-gray-200 p-2 mr-4 rounded-md w-10 text-gray-600 font-mono text-sm text-right">
                    {Array.from({ length: 35 }).map((_, index) => (
                      <div key={index}>{index + 1}</div>
                    ))}
                  </div>
                  <textarea
                    placeholder="Write your code here..."
                    className="flex-grow bg-gray-100 p-2 rounded-md border border-gray-300 focus:outline-none font-mono text-sm text-gray-700"
                    style={{ minHeight: '300px' }}
                  />
                </div>
              </div>
            )}
          </div>
        </ResizablePanel>

        <ResizableHandle withHandle />

        <ResizablePanel defaultSize={75} minSize={20}>
          <div className="relative h-full bg-gray-50 overflow-hidden"
                onClick={handlePanelClick}>
            <div className="w-full h-full bg-[url('https://www.transparenttextures.com/patterns/45-degree-fabric-light.png')] bg-repeat"></div>
            
            {/* {blocks.map((block, index) => (
              <Draggable key={index} defaultPosition={{ x: block.x, y: block.y }}>
                <div>
                  <Block />
                </div>
              </Draggable>
            ))} */}
            <Draggable>
              <div className="text-gray-800 absolute top-10 right-100 w-24 h-24 bg-green-300 rounded-full shadow-md flex items-center justify-center cursor-pointer">
                <Block/>
              </div>
            </Draggable>
            <Draggable>
              <div className="text-gray-800 absolute top-10 right-100 w-24 h-24 bg-green-300 rounded-full shadow-md flex items-center justify-center cursor-pointer">
                <CheckBlock/>
              </div>
            </Draggable>
            <Draggable>
              <div className="text-gray-800 absolute top-10 right-100 w-24 h-24 bg-green-300 rounded-full shadow-md flex items-center justify-center cursor-pointer">
                <Predicate/>
              </div>
            </Draggable>
  
            <Draggable>
              <div className="text-gray-800 absolute top-10 right-100 w-24 h-24 bg-green-300 rounded-full shadow-md flex items-center justify-center cursor-pointer">
                <p>Start</p>
              </div>
            </Draggable>
            <Draggable>
              <div className="text-gray-800 absolute top-20 left-15 w-24 h-24 bg-green-300 rounded-full shadow-md flex items-center justify-center cursor-pointer">
                <p>Finish</p>
              </div>
            </Draggable>
          </div>
        </ResizablePanel>
      </ResizablePanelGroup>
    </div>
  );
};

export default NewProcessPage;
