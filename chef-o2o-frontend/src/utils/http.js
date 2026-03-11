import axios from "axios";
import { storage } from "@/utils/storage";

const http = axios.create({
    baseURL: '',
    timeout: 30000,
});

http.interceptors.request.use((config) => {
    const token = storage.getToken();
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
});

export default http;