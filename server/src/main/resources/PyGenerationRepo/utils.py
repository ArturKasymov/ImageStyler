import torchvision.transforms as T
import numpy as np
import PIL
import torch


class Utils(object):

    @staticmethod
    def getDtype():
        return torch.FloatTensor

    @staticmethod
    def getMean():
        return [0.485, 0.456, 0.406]

    @staticmethod
    def getStd():
        return [0.229, 0.224, 0.225]

    @staticmethod
    def preprocess(img, size=227):
        transform = T.Compose([
            T.Resize(size),
            T.ToTensor(),
            T.Normalize(mean=Utils.getMean(), std=Utils.getStd()),
            T.Lambda(lambda x: x[None])
        ])
        return transform(img)

    @staticmethod
    def deprocess(img):
        transform = T.Compose([
            T.Lambda(lambda x: x[0]),
            T.Normalize(mean=[0, 0, 0], std=[1.0 / s for s in Utils.getStd()]),
            T.Normalize(mean=[-m for m in Utils.getMean()], std=[1, 1, 1]),
            T.Lambda(Utils.rescale),
            T.ToPILImage()
        ])
        return transform(img)

    @staticmethod
    def rescale(x):
        low, high = x.min(), x.max()
        return (x - low) / (high - low)

    @staticmethod
    def extract_features(x, net):
        features = []
        prev_activation = x
        for i, module in enumerate(net._modules.values()):
            next_activation = module(prev_activation)
            features.append(next_activation)
            prev_activation = next_activation
        return features

    @staticmethod
    def contentLoss(content_weight, forward_activation, content_activation):
        F = forward_activation.view(forward_activation.size(1), -1)
        P = content_activation.view(forward_activation.size(1), -1)
        return content_weight * ((F-P)**2).sum()

    @staticmethod
    def gramMatrix(features, normalize=True):
        F = features.view(features.size(0), features.size(1), features.size(2)*features.size(3))
        N, C = features.size(0), features.size(1)
        G = torch.zeros(N, C, C)
        for i in range(N):
            G[i] = torch.mm(F[i], F[i].t())
        if normalize:
            G /= F.size(2)*C
        return G

    @staticmethod
    def styleLoss(features, style_layers, style_activations, style_weights):
        loss = 0.0
        i = 0
        for layer in style_layers:
            loss += style_weights[i] * torch.sum((Utils.gramMatrix(features[layer])-style_activations[i])**2)
            i += 1
        return loss

    @staticmethod
    def tvLoss(img, tv_weight):
        return tv_weight * (torch.sum((img[:, :, 1:, :] - img[:, :, :-1, :])**2) +
                            torch.sum((img[:, :, :, 1:] - img[:, :, :, :-1])**2))
