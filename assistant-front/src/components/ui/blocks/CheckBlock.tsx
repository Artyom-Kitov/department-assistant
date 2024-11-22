import { useState } from "react";
import { TiDeleteOutline } from "react-icons/ti";

const CheckBlock: React.FC = () => {
    const [items, setItems] = useState([{ id: 1 }]);
  
    const handleAdd = () => {
      setItems([...items, { id: items.length + 1 }]);
    };
  
    const handleDelete = (id: number) => {
      setItems(items.filter(item => item.id !== id));
    };
  
    return (
      <div className="w-72 rounded-lg border border-gray-300 shadow-md">
        <div className="bg-gray-300 p-2 rounded-t-lg font-bold text-gray-700">Check Block</div>
        <div className="bg-white p-2 space-y-2 rounded-b-lg">
          {items.map((item, index) => (
            <div key={item.id} className="flex items-center space-x-2">
              <input type="checkbox" 
               className="appearance-none w-6 h-6 rounded-md border-2 border-gray-300 bg-white checked:bg-green-500 checked:border-green-500 focus:outline-none"/>
              <input
                type="text"
                placeholder="To do..."
                className="flex-grow border rounded px-2 py-1 focus:outline-none"
              />
              {index > 0 && (
                <button onClick={() => handleDelete(item.id)} className="text-red-500">
                  <TiDeleteOutline size={20} />
                </button>
              )}
            </div>
          ))}
          <button
            onClick={handleAdd}
            className="w-full bg-gray-200 text-gray-700 rounded py-1 mt-2 font-semibold"
          >
            +
          </button>
        </div>
      </div>
    );
};

export default CheckBlock;