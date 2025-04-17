import axios, { AxiosError } from "axios";
import config from "@/next.config";
import { endpoints } from "@/app/auth/_apis/endpoints";
import { NextResponse } from "next/server";

export const POST = async (request: Request) => {
  try {
    const formData = await request.json();
    const response = await axios.post(
      `${config.api.endpoints}${endpoints.signUp}`,
      formData
    );

    console.log("API Response:", response.data); // Debugging

    if (response.data.headers?.status_code === "200") {
      return NextResponse.json(response.data);
    } else {
      return NextResponse.json({
        success: false,
        error: response.data?.headers?.message || "Signup failed",
      });
    }
  } catch (error: unknown) {
    if (error instanceof AxiosError) {

      return NextResponse.json(
        {
          success: false,
          error:
            error.response?.data||
            error.response?.data?.message ||
            "Internal Server Error",
        },
        { status: error.response?.status || 500 }
      );
    }

    return NextResponse.json(
      { success: false, error: "An unexpected error occurred" },
      { status: 500 }
    );
  }
};
