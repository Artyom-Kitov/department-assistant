import { useForm, SubmitHandler } from "react-hook-form";
import { createNoCriminalCertificateForEmployee, updateNoCriminalCertificateForEmployee } from "@/api";
import { useEffect, useState } from "react";

interface CertificateFormInputs {
  dateOfReceipt: string;
  expirationDate: string;
}

interface CertificateFormProps {
  employeeId: string;
  certificateData?: { dateOfReceipt: string; expirationDate: string };
}

export default function CertificateOfNoCriminalRecordForm({ employeeId, certificateData }: CertificateFormProps) {
  const { register, handleSubmit, setValue } = useForm<CertificateFormInputs>();
  const [isExistingData, setIsExistingData] = useState<boolean>(false);

  useEffect(() => {
    if (certificateData) {
      setValue("dateOfReceipt", certificateData.dateOfReceipt || "");
      setValue("expirationDate", certificateData.expirationDate || "");
      setIsExistingData(true);
    }
  }, [certificateData, setValue]);

  const onSubmit: SubmitHandler<CertificateFormInputs> = async (data) => {
    try {
      if (isExistingData) {
        await updateNoCriminalCertificateForEmployee(employeeId, data);
        alert("Справка о несудимости успешно обновлена");
      } else {
        await createNoCriminalCertificateForEmployee(employeeId, data);
        alert("Справка о несудимости успешно добавлена");
      }
    } catch (error) {
      console.error("Ошибка при обновлении/создании справки о несудимости:", error);
      alert("Ошибка при обновлении/создании справки о несудимости");
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="bg-gray-100 p-6 rounded-lg">
      <h3 className="text-lg font-semibold text-gray-600 mb-4">Справка об отсутствии судимости</h3>

      <label className="text-xs text-gray-500">Дата получения</label>
      <input
        {...register("dateOfReceipt")}
        type="date"
        className="w-full p-2 mb-2 border rounded"
      />

      <label className="text-xs text-gray-500">Дата окончания</label>
      <input
        {...register("expirationDate")}
        type="date"
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
