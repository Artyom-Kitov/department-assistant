import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { IoExitOutline, IoSaveOutline } from "react-icons/io5";
import { getTemplateById, createTemplate, updateTemplate } from "@/api"; 
import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "@/components/ui/shadcn/resizable";
import GraphEditor from "./GraphEditor";
import CodeEditor from "./CodeEditor";

const NewProcessPage: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [code, setCode] = useState<string>("");
  const [processName, setProcessName] = useState("");  

  // Получаем id шаблона из URL
  const searchParams = new URLSearchParams(location.search);
  const templateId = searchParams.get("id");

  useEffect(() => {
    console.log("templateId:", templateId);
    if (templateId) {
      const fetchTemplateCode = async () => {
        try {
          const template = await getTemplateById(templateId);
          setCode(JSON.stringify(template, null, 2));
          setProcessName(template.name);
        } catch (error) {
          console.error("Error loading template:", error);
        }
      };

      fetchTemplateCode();
    }
  }, [templateId]);

  const handleCreateTemplate = async () => {
    try {
      if (templateId) {
        await updateTemplate(templateId, code);
        console.log("Template updated:", code);
      } else {
        const newTemplate = await createTemplate(code);
        console.log("Template created:", newTemplate);
      }
      navigate("/processes/createprocess"); 
    } catch (error) {
      console.error("Error creating template:", error);
    }
  };

  const getActiveClass = (path: string) =>
    location.pathname === path ? "bg-green-500 text-white" : "text-gray-700";

  return (
    <div>
      <div className="fixed top-0 left-1/2 p-2 z-10 flex justify-center transform -translate-x-1/2">
        <div className="flex p-1 bg-slate-200 rounded-full shadow-md">
          <button
            onClick={handleCreateTemplate}
            className={`px-4 py-2 rounded-full transition-all duration-300 ${getActiveClass("/createprocess")}`}
          >
            <IoSaveOutline size={25} />
          </button>

          <input
            type="text"
            placeholder="Untitled..."
            className="flex-grow mx-2 px-4 py-2 rounded-md border border-gray-300 focus:outline-none focus:ring-2 focus:ring-green-500"
            value={processName}
            onChange={(e) => setProcessName(e.target.value)}
          />

          <button
            onClick={() => navigate("/processes/createprocess")}
            className={`px-4 py-2 rounded-full transition-all duration-300 ${getActiveClass("/createprocess")}`}
          >
            <IoExitOutline size={25} />
          </button>
        </div>
      </div>

      <ResizablePanelGroup direction="horizontal" className="min-h-screen rounded-lg border md:min-w-[450px]">
        <ResizablePanel defaultSize={25} minSize={20} maxSize={50}>
          <CodeEditor code={code} setCode={setCode} /> 
        </ResizablePanel>

        <ResizableHandle withHandle />

        <ResizablePanel defaultSize={75} minSize={20}>
          <GraphEditor code={code} setCode={setCode} processName={processName} /> 
        </ResizablePanel>
      </ResizablePanelGroup>
    </div>
  );
};

export default NewProcessPage;
