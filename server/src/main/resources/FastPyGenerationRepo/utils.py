import torchvision.transforms as T
import cv2
import torch


class Utils(object):

    @staticmethod
    def preprocess(img):
        transform = T.Compose([
            T.ToTensor()
        ])
        return transform(img).unsqueeze(dim=0)

    @staticmethod
    def deprocess(tensor):
        tensor = tensor.squeeze()
        img = tensor.cpu().numpy()
        return img.transpose(1, 2, 0)

    @staticmethod
    def save_img(img, image_path):
        img = img.clip(0, 255)
        cv2.imwrite(image_path, img)
