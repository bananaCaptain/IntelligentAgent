// IIntelligentService.aidl
package com.plantain.intelligentservice;

interface IIntelligentService {
    String getVerificationString();
    String getLlamaSystemInfo();
    int loadLlamaModel(String modelPath, int nCtx);
    String chatWithLlama(String prompt);
}
