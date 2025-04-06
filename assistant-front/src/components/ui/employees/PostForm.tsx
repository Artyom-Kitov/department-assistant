import { useForm, SubmitHandler } from "react-hook-form";
import { createPost, updatePost } from "@/api";
import { useEffect, useState } from "react";

interface PostFormInputs {
  name: string;
}

interface PostFormProps {
  postId?: string;
  postData?: { name: string };
}

export default function PostForm({ postId, postData }: PostFormProps) {
  const { register, handleSubmit, setValue } = useForm<PostFormInputs>();
  const [isExistingData, setIsExistingData] = useState<boolean>(false);

  useEffect(() => {
    if (postData) {
      setValue("name", postData.name || "");
      setIsExistingData(true);
    }
  }, [postData, setValue]);

  const onSubmit: SubmitHandler<PostFormInputs> = async (data) => {
    try {
      if (isExistingData && postId) {
        await updatePost(postId, data);
        alert("Должность успешно обновлена");
      } else {
        await createPost(data);
        alert("Должность успешно добавлена");
      }
    } catch (error) {
      console.error("Ошибка при обновлении/создании должности:", error);
      alert("Ошибка при обновлении/создании должности");
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="bg-gray-100 p-6 rounded-lg">
      <h3 className="text-lg font-semibold text-gray-600 mb-4">Должность</h3>

      <label className="text-xs text-gray-500">Название</label>
      <input
        {...register("name")}
        placeholder="Название должности"
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
