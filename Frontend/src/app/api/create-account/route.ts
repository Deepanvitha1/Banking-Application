import { NextResponse } from "next/server";
import axios, { AxiosError } from "axios";
import { cookies } from "next/headers";
import config from "@/next.config";
import { endpoints } from "@/app/auth/_apis/endpoints";

export const POST = async (req: Request) => {
  try {
    const { type_of_account } = await req.json();
    const cookieStore = cookies();
    const token = (await cookieStore).get("token")?.value;
    const userId = (await cookieStore).get("userId")?.value;

    if (!token || !userId || !type_of_account) {
      return NextResponse.json(
        { success: false, error: "Missing required fields" },
        { status: 400 }
      );
    }

    const response = await axios.post(
      `${config.api.endpoints}${endpoints.create}`,
      { uid: userId, type_of_account },
      { headers: { Authorization: `Bearer ${token}` } }
    );

    return NextResponse.json({ success: true, data: response.data });
  } catch (error) {
    if (error instanceof AxiosError) {
      return NextResponse.json(
        {
          success: false,
          error: error.response?.data || "Failed to create account.",
        },
        { status: error.response?.status || 500 }
      );
    }

    return NextResponse.json(
      { success: false, error: "An unexpected error occurred." },
      { status: 500 }
    );
  }
};
