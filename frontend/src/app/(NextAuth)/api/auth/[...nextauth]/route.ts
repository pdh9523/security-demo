import NextAuth, { NextAuthOptions } from "next-auth";
import CredentialsProvider from "next-auth/providers/credentials";
import api from "@/lib/api";
import { AxiosResponse } from "axios";

// LoginResponse 타입 정의
interface LoginResponse {
    accessToken: string;
    refreshToken: string;
}

// next-auth 타입 확장
declare module "next-auth" {
    interface Session {
        accessToken: string;
        refreshToken: string;
    }

    interface User {
        accessToken: string;
        refreshToken: string;
    }

    interface JWT {
        accessToken: string;
        refreshToken: string;
    }
}

const options: NextAuthOptions = {
    providers: [
        CredentialsProvider({
            name: "Credentials",
            credentials: {
                email: { label: "Email", type: "email", placeholder: "example@example.com" },
                password: { label: "Password", type: "password" },
            },
            async authorize(credentials) {
                console.log("ㅎㅇ")
                if (!credentials?.email || !credentials.password) {
                    throw new Error("Missing credentials");
                }

                try {
                    const response: AxiosResponse<LoginResponse> = await api.post("/user/login", {
                        email: credentials.email,
                        password: credentials.password,
                    });

                    if (response.status === 200) {
                        const { accessToken, refreshToken } = response.data;
                        console.log(response.data)
                        // 로그인 성공 시 accessToken과 refreshToken을 반환
                        return {
                            email: credentials.email,
                            accessToken,
                            refreshToken,
                        };
                    } else {
                        throw new Error("Invalid login credentials");
                    }
                } catch (error) {
                    console.error("Login failed:", error);
                    throw new Error("Login failed");
                }
            },
        }),
    ],
    session: {
        strategy: "jwt",
    },
    callbacks: {
        async jwt({ token, user }) {
            // user가 있을 때만 accessToken과 refreshToken을 추가
            if (user) {
                token.accessToken = user.accessToken;
                token.refreshToken = user.refreshToken;
            }
            return token;
        },
        async session({ session, token }) {
            // token에서 accessToken과 refreshToken을 가져와서 session에 추가
            console.log(session)
            console.log(token)
            if (token.accessToken && token.refreshToken) {
                session.accessToken = token.accessToken as string;
                session.refreshToken = token.refreshToken as string;
            }
            return session;
        },
    },
    pages: {
        signIn: "/user/login",
    },
};

const handler = NextAuth(options);

export { handler as GET, handler as POST };
