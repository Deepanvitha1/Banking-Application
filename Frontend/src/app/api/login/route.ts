import axios, { AxiosError } from "axios";
import { NextResponse } from "next/server";
import config from "@/next.config";
import { endpoints } from "@/app/auth/_apis/endpoints";

export const POST = async (request: Request) => {
  try {
    const formData = await request.json();
    const response = await axios.post(
      `${config.api.endpoints}${endpoints.login}`,
      formData
    );
    console.log(formData);
    return NextResponse.json({ success: true, data: response.data });
  } catch (error: unknown) {
    if (error instanceof AxiosError) {
      return NextResponse.json(
        {
          success: false,
          error:
            error.response?.data?.error ||
            error.response?.data ||
            "Internal Server Error",
        },
        { status: error.response?.status || 500 }
      );
    }
  }
};
