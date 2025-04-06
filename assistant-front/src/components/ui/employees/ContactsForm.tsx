import { useForm, SubmitHandler } from "react-hook-form";
import { createContactsForEmployee, updateContactsForEmployee } from "@/api";
import { useEffect, useState } from "react";

interface ContactsFormInputs {
  phoneNumber: string;
  email: string;
  nsuEmail: string;
  additionalInfo: string;
}

interface ContactsFormProps {
  employeeId: string;
  contacts?: { phoneNumber: string; email: string; nsuEmail: string; additionalInfo: string };
}

export default function ContactsForm({ employeeId, contacts }: ContactsFormProps) {
  const { register, handleSubmit, setValue } = useForm<ContactsFormInputs>();
  const [isExistingData, setIsExistingData] = useState<boolean>(false);

  useEffect(() => {
    if (contacts) {
      setValue("phoneNumber", contacts.phoneNumber || "");
      setValue("email", contacts.email || "");
      setValue("nsuEmail", contacts.nsuEmail || "");
      setValue("additionalInfo", contacts.additionalInfo || "");
      setIsExistingData(true);
    }
  }, [contacts, setValue]);

  const onSubmit: SubmitHandler<ContactsFormInputs> = async (data) => {
    try {
      if (isExistingData) {
        console.log("Updating contacts...");
        await updateContactsForEmployee(employeeId, data);  // Обновление данных
        alert("Контактные данные успешно обновлены");
      } else {
        console.log("Creating contacts...");
        console.log(data);
        await createContactsForEmployee(employeeId, data);  // Создание новых данных
        alert("Контактные данные успешно добавлены");
      }
    } catch (error) {
      console.error("Ошибка при обновлении/создании контактных данных:", error);
      alert("Ошибка при обновлении/создании контактных данных");
    }
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="bg-gray-100 p-6 rounded-lg"
    >
      <h3 className="text-lg font-semibold text-gray-600 mb-4">Контактные данные</h3>

      <label className="text-xs text-gray-500">Телефон</label>
      <input
        {...register("phoneNumber")}
        placeholder="Телефон"
        className="w-full p-2 mb-2 border rounded"
      />

      <label className="text-xs text-gray-500">E-mail</label>
      <input
        {...register("email")}
        placeholder="E-mail"
        className="w-full p-2 mb-2 border rounded"
      />

      <label className="text-xs text-gray-500">NSU E-mail</label>
      <input
        {...register("nsuEmail")}
        placeholder="NSU E-mail"
        className="w-full p-2 mb-2 border rounded"
      />

      <label className="text-xs text-gray-500">Дополнительная информация</label>
      <textarea
        {...register("additionalInfo")}
        placeholder="Дополнительная информация"
        className="w-full p-2 mb-2 border rounded"
      />

      <button
        type="submit"
        className="bg-[#4fff9e] text-gray-700 hover:bg-green-400 px-4 py-2 rounded mt-4 w-full"
      >
        Сохранить
      </button>
    </form>
  );
}
