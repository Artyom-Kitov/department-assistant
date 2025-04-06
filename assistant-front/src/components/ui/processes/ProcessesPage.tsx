import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Navbar from "../Navbar";
import { FaPlusSquare } from "react-icons/fa";
import { ImTree } from "react-icons/im";
import { getTemplates, deleteTemplate } from "@/api";
import { IoIosClose } from "react-icons/io";

interface Template {
  id: string;
  name: string;
  duration: number;
}

export default function ProcessesPage() {
  const [templates, setTemplates] = useState<Template[]>([]);
  const [templateToDelete, setTemplateToDelete] = useState<Template | null>(null);

  useEffect(() => {
    const fetchTemplates = async () => {
      try {
        const data = await getTemplates();
        setTemplates(data);
      } catch (error) {
        console.error("Error fetching templates:", error);
      }
    };

    fetchTemplates();
  }, []);

  const handleDeleteTemplate = async (id: string) => {
    try {
      await deleteTemplate(id);
      setTemplates(templates.filter(template => template.id !== id));
      setTemplateToDelete(null);
    } catch (error) {
      console.error("Error deleting template:", error);
    }
  };

  return (
    <div>
      <Navbar />
      <div className="p-8">
        <h2 className="text-2xl font-semibold mb-4">Новый шаблон</h2>
        <div className="flex items-left justify-start mb-8">
          <Link to="/processes/createprocess/process-editor">
            <div className="flex flex-col items-center justify-center w-36 h-36 bg-gray-100 rounded-lg shadow-md hover:bg-gray-200 transition">
              <FaPlusSquare className="text-gray-500 text-4xl mb-4" />
              <p className="text-lg font-semibold text-gray-600">Новый шаблон</p>
            </div>
          </Link>
        </div>

        <h2 className="text-2xl font-semibold mb-4">Готовые шаблоны</h2>
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
          {templates.map((template) => (
            <div
              key={template.id}
              className="relative flex flex-col items-center justify-center w-48 h-48 bg-gray-100 rounded-lg shadow-md hover:bg-gray-200 transition"
            >
              <IoIosClose
                className="absolute top-2 right-2 text-gray-500 hover:text-gray-700 text-2xl cursor-pointer"
                onClick={() => setTemplateToDelete(template)}
              />
              <Link to={`/processes/createprocess/process-editor?id=${template.id}`} className="flex flex-col items-center justify-center w-full h-full">
                <ImTree className="text-gray-500 text-4xl mb-4" />
                <p className="text-lg font-semibold text-gray-600">
                  {template.name}
                </p>
              </Link>
            </div>
          ))}
        </div>
      </div>

      {templateToDelete && (
        <div className="fixed inset-0 flex items-center justify-center bg-gray-800 bg-opacity-50">
          <div className="bg-white p-6 rounded-lg">
            <p className="mb-4">
              Вы действительно хотите удалить шаблон <br/> <b>{templateToDelete.name}</b>?
            </p>
            <div className="flex justify-end">
              <button
                className="mr-2 px-4 py-2 bg-gray-300 rounded"
                onClick={() => setTemplateToDelete(null)}
              >
                Нет
              </button>
              <button
                className="px-4 py-2 bg-red-500 text-white rounded"
                onClick={() => handleDeleteTemplate(templateToDelete.id)}
              >
                Да
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
