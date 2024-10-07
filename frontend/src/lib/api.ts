import axios from "axios";
import { getSession } from "next-auth/react";

export const api = axios.create({
    baseURL: `${process.env.NEXT_PUBLIC_SERVER_URL}`,
    timeout: 5000,
});

// 요청 인터셉터 설정
api.interceptors.request.use(
    async (config) => {
        const session = await getSession(); // 세션에서 accessToken 가져오기
        if (session?.accessToken) {
            config.headers['Authorization'] = `Bearer ${session.accessToken}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);